/*
 * This file is part of internal-code-gen-processor, licensed under the MIT License.
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
package net.strokkur.internal.processor;

import com.google.auto.service.AutoService;
import net.strokkur.internal.annotations.ModifyClass;
import net.strokkur.jap.code.CodeGenUtil;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.classmodel.CodeRecord;
import net.strokkur.jap.code.classmodel.CodeRecordComponent;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.source.SourceMapProcessor;
import net.strokkur.jap.source.SourceMapUtil;
import net.strokkur.jap.source.annotation.SourceAnnotation;
import net.strokkur.jap.source.classmodel.SourceClassLike;
import net.strokkur.jap.source.classmodel.SourceInterface;
import net.strokkur.jap.source.classmodel.SourceRecord;
import net.strokkur.jap.source.classmodel.SourceRecordComponent;
import net.strokkur.jap.source.type.SourceType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AutoService(Processor.class)
public class InternalProcessor extends AbstractProcessor implements SourceMapProcessor {
  private final SourceMapUtil MAP = new SourceMapUtil(this);
  private final CodeGenUtil GEN = new CodeGenUtil(this);

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getElementsAnnotatedWith(ModifyClass.class)) {
      if (!(element instanceof TypeElement typeElement)) {
        continue;
      }

      final SourceClassLike like = MAP.parseClassElement(typeElement);
      if (like instanceof SourceRecord rec) {
        final CodeRecord codeRecord = toCode(rec);
        final List<CodeMethod> methods = new ArrayList<>(codeRecord.methods());
        methods.add(CodeMethod.builder("someCustomMethod")
          .setReturnType(JavaTypes.STRING)
          .addModifiers(Modifiers.PUBLIC)
          .setCode(
            Statements.returnStmt(Expressions.string("lol, no"))
          )
          .toMethod()
        );

        final CodeRecord newRecord = new CodeRecord(
          CodeTypes.of("generated." + codeRecord.classType().identifiableName() + "A"),
          codeRecord.genericTypes(),
          codeRecord.modifiers(),
          codeRecord.annotations().stream()
            .filter(anno -> !anno.type().isType(CodeTypes.of(ModifyClass.class)))
            .toList(),
          codeRecord.implementsTypes(),
          codeRecord.components(),
          codeRecord.fields(),
          methods,
          codeRecord.primaryConstructor(),
          codeRecord.additionalConstructors(),
          codeRecord.documentation()
        );
        try {
          GEN.printJavaFile(newRecord, typeElement);
        } catch (IOException e) {
          messager().errorSource("Failed to print Java file", e, rec);
        }
      }
    }

    return false;
  }

  private CodeRecord toCode(SourceRecord record) {
    final List<String> recordComponentNames = record.components().stream().map(SourceRecordComponent::name).toList();
    return new CodeRecord(
      record.classType(),
      record.genericTypes(),
      record.modifiers(),
      record.annotations().stream().map(SourceAnnotation::toAnnotation).toList(),
      record.implementsClasses().stream().map(SourceInterface::classType).toList(),
      record.components().stream().map(comp -> new CodeRecordComponent(
        comp.type().toType(),
        comp.name(),
        comp.annotations().stream().map(SourceAnnotation::toAnnotation).toList()
      )).toList(),
      List.of(),
      List.of(),
      null,
      List.of(),
      null
    );
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(ModifyClass.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latest();
  }

  @Override
  public ProcessingEnvironment processingEnv() {
    return Objects.requireNonNull(this.processingEnv);
  }
}
