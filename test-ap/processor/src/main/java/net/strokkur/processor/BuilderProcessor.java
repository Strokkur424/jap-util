/*
 * This file is part of test-ap-processor, licensed under the MIT License.
 *
 * Copyright (c) 2026 Strokkur24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.strokkur.processor;

import net.strokkur.jap.code.CodeGenUtil;
import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.builder.ClassBuilder;
import net.strokkur.jap.code.classmodel.builder.FieldBuilder;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.util.StyleConfig;
import net.strokkur.jap.source.SourceMapProcessor;
import net.strokkur.jap.source.SourceMapUtil;
import net.strokkur.jap.source.classmodel.SourceClassLike;
import net.strokkur.jap.source.classmodel.SourceRecord;
import net.strokkur.jap.source.classmodel.SourceRecordComponent;
import net.strokkur.jap.source.type.SourcePrimitiveType;
import net.strokkur.processor.annotations.CreateBuilder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BuilderProcessor extends AbstractProcessor implements SourceMapProcessor {
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(CreateBuilder.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(CreateBuilder.class);
    final SourceMapUtil mapUtil = new SourceMapUtil(this);
    final CodeGenUtil genUtil = new CodeGenUtil(this);

    for (Element element : annotatedElements) {
      final SourceClassLike classLike = mapUtil.parseClassElement((TypeElement) element);
      if (!(classLike instanceof SourceRecord record)) {
        messager().errorSource("Not a record", classLike);
        continue;
      }

      final CodeClassType builderType = CodeTypes.ofClass(classLike.classType().fullyQualifiedName() + "Builder");
      final ClassBuilder builder = new ClassBuilder(builderType);
      builder.addModifiers(Modifiers.PUBLIC, Modifiers.FINAL);

      builder.addMethods(CodeMethod.builder("create")
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
        .setReturnType(builderType)
        .setCodeBlock(
          Statements.returnStmt(builderType.ctor())
        )
      );

      final List<ConvertToExpression> ctorParameters = new ArrayList<>();
      for (SourceRecordComponent component : record.components()) {
        // Add field to class
        final FieldBuilder fieldBuilder = CodeField.builder(
          component.type(), component.name()
        );
        fieldBuilder.addModifiers(Modifiers.PRIVATE);

        if (component.hasAnnotation(DefaultsAnnotations.FOR_STRING)) {
          fieldBuilder.setInitializer(
            component.firstAnnotationByType(DefaultsAnnotations.FOR_STRING)
              .parameter("value")
              .value()
          );
        } else if (component.hasAnnotation(DefaultsAnnotations.FOR_INT)) {
          fieldBuilder.setInitializer(
            component.firstAnnotationByType(DefaultsAnnotations.FOR_INT)
              .parameter("value")
              .value()
          );
        } else if (!(component.type() instanceof SourcePrimitiveType)) {
          fieldBuilder.addAnnotations(JSpecifyTypes.NULLABLE);
        }
        builder.addFields(fieldBuilder);

        // Add constructor parameter to list
        if (component.hasAnnotation(DefaultsAnnotations.CAN_BE_NULL)
          || component.type() instanceof SourcePrimitiveType) {
          ctorParameters.add(Expressions.fieldAccess(component.name()));
        } else {
          ctorParameters.add(JavaTypes.OBJECTS.chainMethod("requireNonNull")
            .addParameters(Expressions.fieldAccess(component.name()))
          );
        }

        final String uppercaseName = Character.toUpperCase(component.name().charAt(0)) + component.name().substring(1);
        final CodeAnnotation[] paramAnnotations = component.hasAnnotation(JSpecifyTypes.NULLABLE)
          ? new CodeAnnotation[]{CodeAnnotation.of(JSpecifyTypes.NULLABLE)}
          : new CodeAnnotation[0];

        // Add set and get methods.
        builder.addMethods(CodeMethod.builder("set" + uppercaseName)
          .setReturnType(builderType)
          .addModifiers(Modifiers.PUBLIC)
          .addParameter(component.type(), component.name(), paramAnnotations)
          .setCodeBlock(
            Expressions.thisExpr().chainField(component.name()).assign(Expressions.variable(component.name())),
            Statements.returnStmt(Expressions.thisExpr())
          )
        );
        builder.addMethods(CodeMethod.builder("get" + uppercaseName)
          .setReturnType(component.type())
          .addModifiers(Modifiers.PUBLIC)
          .setCodeBlock(
            Statements.returnStmt(Expressions.thisExpr().chainField(component.name()))
          )
        );
      }

      builder.addMethods(CodeMethod.builder("build")
        .setReturnType(classLike)
        .addModifiers(Modifiers.PUBLIC)
        .setCodeBlock(
          Statements.returnStmt(
            classLike.ctor(ctorParameters.toArray(ConvertToExpression[]::new))
              .setStyle(StyleConfig.MULTILINE)
          )
        )
      );

      try {
        genUtil.printJavaFile(builder.build(), element);
      } catch (IOException e) {
        processingEnv.getMessager().printError("Failed to print Java file: " + e.getMessage(), element);
      }
    }

    return true;
  }

  @Override
  public ProcessingEnvironment processingEnv() {
    return this.processingEnv;
  }

  interface DefaultsAnnotations extends ConvertToClassType {
    DefaultsAnnotations FOR_STRING = create("ForString");
    DefaultsAnnotations FOR_INT = create("ForInt");
    DefaultsAnnotations CAN_BE_NULL = create("CanBeNull");

    static DefaultsAnnotations create(String name) {
      return () -> CodeTypes.ofClass("net.strokkur.processor.annotations.BuilderDefault$" + name);
    }
  }
}
