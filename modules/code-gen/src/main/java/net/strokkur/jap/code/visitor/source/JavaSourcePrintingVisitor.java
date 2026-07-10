/*
 * This file is part of code-gen, licensed under the MIT License.
 *
 * Copyright (c) 2025 Strokkur24
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
package net.strokkur.jap.code.visitor.source;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.annotations.CodeAnnotationParameter;
import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.classmodel.MethodLike;
import net.strokkur.jap.code.documentation.AbstractDocumentationRenderer;
import net.strokkur.jap.code.expression.AssignExpression;
import net.strokkur.jap.code.expression.BooleanExpression;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.ConstructorInvocation;
import net.strokkur.jap.code.expression.FieldAccess;
import net.strokkur.jap.code.expression.InstanceOfExpr;
import net.strokkur.jap.code.expression.MethodInvocation;
import net.strokkur.jap.code.expression.MethodReference;
import net.strokkur.jap.code.expression.MultilineLambda;
import net.strokkur.jap.code.expression.SingleLineLambda;
import net.strokkur.jap.code.expression.UnaryMinusExpression;
import net.strokkur.jap.code.expression.simple.CodeBooleanExpression;
import net.strokkur.jap.code.expression.simple.CodeDoubleExpression;
import net.strokkur.jap.code.expression.simple.CodeFloatExpression;
import net.strokkur.jap.code.expression.simple.CodeIntExpression;
import net.strokkur.jap.code.expression.simple.CodeLongExpression;
import net.strokkur.jap.code.expression.simple.CodeNullExpression;
import net.strokkur.jap.code.expression.simple.CodeStringExpression;
import net.strokkur.jap.code.expression.simple.CodeVariableExpression;
import net.strokkur.jap.code.expression.source.FieldMethodSource;
import net.strokkur.jap.code.expression.source.MethodReferenceSource;
import net.strokkur.jap.code.statement.BlankStatement;
import net.strokkur.jap.code.statement.CodeStatement;
import net.strokkur.jap.code.statement.ExpressionStatement;
import net.strokkur.jap.code.statement.IfStatement;
import net.strokkur.jap.code.statement.ReturnStatement;
import net.strokkur.jap.code.statement.ThrowStatement;
import net.strokkur.jap.code.statement.VariableDeclarationStatement;
import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.util.StyleConfig;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JavaSourcePrintingVisitor extends AbstractSourcePrintingVisitor {
  public JavaSourcePrintingVisitor(Supplier<AbstractDocumentationRenderer> javadocPrintingVisitor, String indent, String continuationIndent) {
    super(javadocPrintingVisitor, indent, continuationIndent);
  }

  //<editor-fold desc="Utilities"
  private StringBuilder visitMethodLike(MethodLike method, Supplier<StringBuilder> typeAndName) {
    return append(builder -> {
      printDocumentationIndented(builder, method.documentation());
      printAnnotationsIndented(builder, method.annotations());
      printModifiersIndented(builder, method.modifiers());

      if (!method.generics().isEmpty()) {
        builder.append("<");
        builder.append(joining(method.generics()));
        builder.append("> ");
      }

      builder.append(typeAndName.get());

      builder.append("(");
      builder.append(joining(method.parameters()));
      builder.append(")");

      if (!method.throwsExceptions().isEmpty()) {
        builder.append(" throws ");
        builder.append(joining(method.throwsExceptions()));
      }

      builder.append(" {\n");
      builder.append(method.codeBlock().accept(this));
      appendIndent(builder);
      builder.append("}\n");
    });
  }
  //</editor-fold>

  @Override
  public StringBuilder visitAnnotation(CodeAnnotation annotation) {
    return append(builder -> {
      builder.append("@").append(annotation.type().accept(this));
      if (!annotation.parameters().isEmpty()) {
        builder.append("(");
        if (annotation.parameters().size() == 1 && annotation.parameters().getFirst().name().equals("value")) {
          builder.append(annotation.parameters().getFirst().value().accept(this));
        } else {
          builder.append(joining(annotation.parameters()));
        }
        builder.append(")");
      }
    });
  }

  @Override
  public StringBuilder visitAnnotationParameter(CodeAnnotationParameter param) {
    return append(builder -> builder.append(param.name()).append(" = ").append(param.value().accept(this)));
  }

  @Override
  public StringBuilder visitClass(CodeClass codeClass) {
    class ClassPrintUtil {
      private void printSpaced(StringBuilder builder, List<? extends CodeVisitable> methods) {
        methods.forEach(method -> {
          builder.append("\n");
          appendNested(builder, method);
        });
      }
    }

    final ClassPrintUtil util = new ClassPrintUtil();
    return append(builder -> {
      printDocumentationIndented(builder, codeClass.documentation());
      printAnnotationsIndented(builder, codeClass.annotations());
      printModifiersIndented(builder, codeClass.modifiers());
      builder.append("class ");
      builder.append(codeClass.classType().name());

      if (!codeClass.genericTypes().isEmpty()) {
        builder.append("<");
        builder.append(joining(codeClass.genericTypes()));
        builder.append(">");
      }

      builder.append(" {\n");

      appendIndented(() -> {
        final List<CodeField> staticFields = codeClass.fields().stream()
          .filter(field -> field.modifiers().contains(Modifiers.STATIC))
          .toList();
        staticFields.forEach(field -> appendNested(builder, field));

        final List<CodeField> instanceFields = codeClass.fields().stream()
          .filter(field -> !field.modifiers().contains(Modifiers.STATIC))
          .toList();

        if (!staticFields.isEmpty() && !instanceFields.isEmpty()) {
          builder.append("\n");
        }
        instanceFields.forEach(field -> appendNested(builder, field));

        final List<CodeMethod> staticMethods = codeClass.methods().stream()
          .filter(method -> method.modifiers().contains(Modifiers.STATIC))
          .toList();
        util.printSpaced(builder, staticMethods);

        final List<CodeConstructor> constructors = codeClass.constructors();
        util.printSpaced(builder, constructors);

        final List<CodeMethod> instanceMethods = codeClass.methods().stream()
          .filter(Predicate.not(method -> method.modifiers().contains(Modifiers.STATIC)))
          .toList();
        util.printSpaced(builder, instanceMethods);
      });

      appendIndent(builder);
      builder.append("}\n");
    });
  }

  @Override
  public StringBuilder visitConstructor(CodeConstructor ctor) {
    return visitMethodLike(ctor, () -> ctor.type().accept(this));
  }

  @Override
  public StringBuilder visitMethod(CodeMethod method) {
    return visitMethodLike(method, () -> append(builder -> {
      builder.append(method.returnType().accept(this));
      builder.append(" ");
      builder.append(method.name());
    }));
  }

  @Override
  public StringBuilder visitField(CodeField field) {
    return append(builder -> {
      printModifiersIndented(builder, field.modifiers());
      if (!field.annotations().isEmpty()) {
        builder.append(joining(field.annotations()));
        builder.append(" ");
      }
      appendNested(builder, field.type());
      builder.append(" ");
      builder.append(field.name());
      if (field.initializer() != null) {
        builder.append(" = ");
        appendNested(builder, field.initializer());
      }
      builder.append(";\n");
    });
  }

  @Override
  public StringBuilder visitParameterDefinition(CodeParameterDefinition parameter) {
    return append(builder -> {
      if (parameter.hasAnnotations()) {
        final String annotationString = parameter.annotations().stream()
          .map(anno -> anno.accept(this).toString())
          .collect(Collectors.joining(" "));
        builder.append(annotationString).append(" ");
      }

      builder.append(parameter.type().accept(this));
      builder.append(" ").append(parameter.name());
    });
  }

  @Override
  public StringBuilder visitExpression(CodeExpression expression) {
    return append(builder -> {
      switch (expression) {
        case CodeBooleanExpression(boolean value) -> builder.append(value);

        case CodeDoubleExpression(double value) -> builder.append(value).append("d");

        case CodeFloatExpression(float value) -> builder.append(value).append("f");

        case CodeIntExpression(int value) -> builder.append(value);

        case CodeLongExpression(long value) -> builder.append(value).append("l");

        case CodeNullExpression ignored -> builder.append("null");

        case CodeStringExpression(String value) -> builder.append('"').append(value).append('"');

        case CodeVariableExpression(String name) -> builder.append(name);

        case AssignExpression(CodeExpression leftSide, CodeExpression rightSide) -> builder
          .append(leftSide.accept(this))
          .append(" = ")
          .append(rightSide.accept(this));

        case ConstructorInvocation(
          CodeClassType type, List<CodeExpression> parameters, @Nullable FieldMethodSource source, StyleConfig style
        ) -> appendIndentedContinuationConditional(source != null && style.newline(), () -> {
          if (source != null) {
            builder.append(source.accept(this));
            if (style.newline()) {
              builder.append("\n");
              appendIndent(builder);
            }
            builder.append(".");
          }
          builder.append("new ");
          builder.append(type.accept(this));
          appendMethodCallParams(builder, parameters, style);
        });

        case FieldAccess(@Nullable FieldMethodSource source, String fieldName) -> {
          if (source == null) {
            builder.append(fieldName);
          } else {
            builder.append(source.accept(this)).append(".").append(fieldName);
          }
        }

        case InstanceOfExpr(
          CodeExpression source, CodeClassType classType, @Nullable String targetVariable, boolean isInverted
        ) -> {
          if (isInverted) {
            builder.append("!(");
          }
          builder.append(source.accept(this));
          builder.append(" instanceof ");
          builder.append(classType.accept(this));
          if (targetVariable != null) {
            builder.append(" ").append(targetVariable);
          }
          if (isInverted) {
            builder.append(")");
          }
        }

        case MethodInvocation(
          String methodName, @Nullable FieldMethodSource source, List<CodeExpression> parameters, StyleConfig style
        ) -> {
          {
            if (source != null) {
              builder.append(source.accept(this));
              if (style.newline()) {
                builder.append("\n");
                appendIndentedContinuation(() -> appendIndent(builder));
              }
              builder.append(".");
            }
            builder.append(methodName);
            appendMethodCallParams(builder, parameters, style);
          }
        }

        case MethodReference(MethodReferenceSource source, String methodName) -> {
          builder.append(source.accept(this))
            .append("::")
            .append(methodName);
        }

        case MultilineLambda(List<String> lambdaParameters, CodeBlock lambdaBlock) -> {
          appendLambdaHead(builder, lambdaParameters);
          builder.append("{\n");
          builder.append(lambdaBlock.accept(this));
          appendIndent(builder);
          builder.append("}");
        }

        case SingleLineLambda(List<String> lambdaParameters, CodeExpression codeExpression) -> {
          appendLambdaHead(builder, lambdaParameters);
          builder.append(codeExpression.accept(this));
        }

        case UnaryMinusExpression(CodeExpression expr) -> builder.append("-").append(expr.accept(this));

        default -> throw new IllegalArgumentException("Unrecognized expression type: " + expression.getClass());
      }
    });
  }

  @Override
  public StringBuilder visitStatement(CodeStatement statement) {
    return append(builder -> {
      if (statement instanceof BlankStatement) {
        builder.append("\n");
        return;
      }
      appendIndent(builder);

      if (statement instanceof IfStatement(BooleanExpression expr, CodeBlock ifTrue, @Nullable CodeBlock ifFalse)) {
        builder.append("if (").append(expr.accept(this)).append(") {\n");
        builder.append(ifTrue.accept(this));
        appendIndent(builder);
        builder.append("}");
        if (ifFalse != null) {
          builder.append(" {\n");
          builder.append(ifFalse.accept(this));
          appendIndent(builder);
          builder.append("}");
        }
        builder.append("\n");
        return;
      }

      switch (statement) {

        case ExpressionStatement(CodeExpression expression) -> builder.append(expression.accept(this));

        case ReturnStatement(@Nullable CodeExpression returnExpression) -> {
          builder.append("return");
          if (returnExpression != null) {
            builder.append(" ").append(returnExpression.accept(this));
          }
        }

        case ThrowStatement(CodeExpression throwExpression) -> {
          builder.append("throw").append(" ").append(throwExpression.accept(this));
        }

        case VariableDeclarationStatement(
          CodeType variableType, String name, @Nullable CodeExpression assignment, boolean isFinal
        ) -> {
          if (isFinal) {
            builder.append("final ");
          }
          builder.append(variableType.accept(this)).append(" ").append(name);
          if (assignment != null) {
            builder.append(" = ").append(assignment.accept(this));
          }
        }

        default -> throw new IllegalArgumentException("Unrecognized statement type: " + statement.getClass());
      }

      builder.append(";\n");
    });
  }

  @Override
  public StringBuilder visitType(CodeType type) {
    return append(builder -> {
      switch (type) {
        case CodeArrayType(CodeType inner) -> builder.append(inner.accept(this)).append("[]");

        case CodeClassType classType -> {
          builder.append(classType.simpleName());
          if (classType.genericTypes() != null) {
            builder.append("<");
            builder.append(joining(classType.genericTypes()));
            builder.append(">");
          }
        }

        case CodeGenericType(@Nullable String genericName, @Nullable GenericEnclosure enclosure) -> {
          if (genericName == null && enclosure instanceof GenericEnclosure.TypeEnclosure(CodeType encloses)) {
            builder.append(encloses.accept(this));
            return;
          }

          builder.append(genericName == null ? "?" : genericName);
          if (enclosure != null) {
            builder.append(" ").append(enclosure.accept(this));
          }
        }

        case CodePrimitiveType(String name, String boxed) -> builder.append(name);

        default -> throw new IllegalArgumentException("Unrecognized type: " + type.getClass());
      }
    });
  }

  @Override
  public StringBuilder visitCodeBlock(CodeBlock block) {
    return append(builder -> appendIndented(() -> {
      for (CodeStatement statement : block.statements()) {
        builder.append(statement.accept(this));
      }
    }));
  }

  @Override
  public StringBuilder visitGenericTypeDefinition(CodeGenericTypeDefinition genericTypeDefinition) {
    return append(builder -> {
      builder.append(genericTypeDefinition.name());
      if (genericTypeDefinition.enclosure() != null) {
        builder.append(" ");
        builder.append(genericTypeDefinition.enclosure().accept(this));
      }
    });
  }

  @Override
  public StringBuilder visitGenericEnclosure(GenericEnclosure enclosure) {
    return append(builder -> {
      switch (enclosure) {
        case GenericEnclosure.ExtendsEnclosure(CodeType encloses) -> {
          builder.append("extends ").append(encloses.accept(this));
        }
        case GenericEnclosure.SuperEnclosure(CodeType encloses) -> {
          builder.append("super ").append(encloses.accept(this));
        }
        case GenericEnclosure.TypeEnclosure(CodeType encloses) -> builder.append(encloses.accept(this));
      }
    });
  }

  private void appendLambdaHead(StringBuilder builder, List<String> lambdaParams) {
    if (lambdaParams.size() != 1) {
      builder.append("(");
      builder.append(String.join(", ", lambdaParams));
      builder.append(")");
    } else {
      builder.append(lambdaParams.getFirst());
    }
    builder.append(" -> ");
  }
}
