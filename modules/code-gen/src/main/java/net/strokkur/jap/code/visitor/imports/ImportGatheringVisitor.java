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
package net.strokkur.jap.code.visitor.imports;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.annotations.CodeAnnotationParameter;
import net.strokkur.jap.code.classmodel.CodeAnnotationType;
import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeClassLike;
import net.strokkur.jap.code.classmodel.CodeClassLikeTyped;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeEnum;
import net.strokkur.jap.code.classmodel.CodeEnumValue;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeInterface;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.classmodel.CodePrimaryConstructor;
import net.strokkur.jap.code.classmodel.CodeRecord;
import net.strokkur.jap.code.classmodel.CodeRecordComponent;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.AssignExpression;
import net.strokkur.jap.code.expression.CastExpression;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.ConcatExpression;
import net.strokkur.jap.code.expression.ConstructorInvocation;
import net.strokkur.jap.code.expression.FieldAccess;
import net.strokkur.jap.code.expression.InstanceOfExpr;
import net.strokkur.jap.code.expression.MethodInvocation;
import net.strokkur.jap.code.expression.MethodReference;
import net.strokkur.jap.code.expression.MultilineLambda;
import net.strokkur.jap.code.expression.SingleLineLambda;
import net.strokkur.jap.code.expression.UnaryMinusExpression;
import net.strokkur.jap.code.expression.bool.AndExpression;
import net.strokkur.jap.code.expression.bool.EqExpression;
import net.strokkur.jap.code.expression.bool.NeqExpression;
import net.strokkur.jap.code.expression.bool.NotExpression;
import net.strokkur.jap.code.expression.bool.OrExpression;
import net.strokkur.jap.code.expression.simple.SimpleExpression;
import net.strokkur.jap.code.statement.BlankStatement;
import net.strokkur.jap.code.statement.CodeStatement;
import net.strokkur.jap.code.statement.CommentStatement;
import net.strokkur.jap.code.statement.ExpressionStatement;
import net.strokkur.jap.code.statement.IfStatement;
import net.strokkur.jap.code.statement.ReturnStatement;
import net.strokkur.jap.code.statement.ThrowStatement;
import net.strokkur.jap.code.statement.TryStatement;
import net.strokkur.jap.code.statement.VariableDeclarationStatement;
import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class ImportGatheringVisitor implements CodeVisitor<GatheredImports> {

  private GatheredImports join(GatheredImports... all) {
    return Stream.of(all).collect(GatheredImports.collector());
  }

  private GatheredImports maybeAccept(@Nullable CodeVisitable visitable) {
    if (visitable != null) {
      return visitable.accept(this);
    } else {
      return GatheredImports.empty();
    }
  }

  private <S extends CodeVisitable> GatheredImports collect(Collection<S> collection) {
    return collection.stream()
      .map(visitable -> visitable.accept(this))
      .collect(GatheredImports.collector());
  }

  private GatheredImports acceptClassLike(CodeClassLike classLike) {
    final GatheredImports base = join(
      classLike.classType().accept(this),
      maybeAccept(classLike.documentation()),
      collect(classLike.methods()),
      collect(classLike.fields()),
      collect(classLike.annotations())
    );
    if (classLike instanceof CodeClassLikeTyped typed) {
      return join(base, collect(typed.genericTypes()));
    } else {
      return base;
    }
  }

  @Override
  public GatheredImports visitClass(CodeClass codeClass) {
    return join(
      acceptClassLike(codeClass),
      collect(codeClass.constructors()),
      maybeAccept(codeClass.extendsType()),
      collect(codeClass.implementsTypes())
    );
  }

  @Override
  public GatheredImports visitInterface(CodeInterface codeInterface) {
    return join(
      acceptClassLike(codeInterface),
      collect(codeInterface.extendsTypes())
    );
  }

  @Override
  public GatheredImports visitEnum(CodeEnum codeEnum) {
    return join(
      acceptClassLike(codeEnum),
      collect(codeEnum.values()),
      collect(codeEnum.implementsTypes())
    );
  }

  @Override
  public GatheredImports visitEnumValue(CodeEnumValue enumValue) {
    return join(collect(enumValue.parameters()), maybeAccept(enumValue.documentation()));
  }

  @Override
  public GatheredImports visitRecord(CodeRecord record) {
    return join(
      acceptClassLike(record),
      collect(record.components()),
      maybeAccept(record.primaryConstructor()),
      collect(record.additionalConstructors()),
      collect(record.implementsTypes())
    );
  }

  @Override
  public GatheredImports visitRecordComponent(CodeRecordComponent recordComponent) {
    return join(
      maybeAccept(recordComponent.type()),
      collect(recordComponent.annotations())
    );
  }

  @Override
  public GatheredImports visitAnnotationType(CodeAnnotationType annotationType) {
    return acceptClassLike(annotationType);
  }

  @Override
  public GatheredImports visitMethod(CodeMethod codeMethod) {
    return join(
      maybeAccept(codeMethod.documentation()),
      collect(codeMethod.parameters()),
      collect(codeMethod.throwsExceptions()),
      codeMethod.returnType().accept(this),
      maybeAccept(codeMethod.codeBlock()),
      maybeAccept(codeMethod.defaults()),
      collect(codeMethod.generics()),
      collect(codeMethod.annotations())
    );
  }

  @Override
  public GatheredImports visitPrimaryConstructor(CodePrimaryConstructor ctor) {
    return join(
      maybeAccept(ctor.documentation()),
      collect(ctor.annotations()),
      collect(ctor.generics()),
      collect(ctor.throwsExceptions()),
      ctor.code().accept(this)
    );
  }

  @Override
  public GatheredImports visitConstructor(CodeConstructor ctor) {
    return join(
      maybeAccept(ctor.documentation()),
      collect(ctor.annotations()),
      collect(ctor.generics()),
      collect(ctor.throwsExceptions()),
      collect(ctor.parameters()),
      ctor.codeBlock().accept(this)
    );
  }

  @Override
  public GatheredImports visitType(CodeType codeType) {
    return switch (codeType) {
      case CodeArrayType arrayType -> join(
        collect(arrayType.annotations()),
        arrayType.inner().accept(this)
      );
      case CodeGenericType genericType -> join(
        collect(genericType.annotations()),
        maybeAccept(genericType.enclosure())
      );
      case CodeClassType classType -> join(
        collect(classType.annotations()),
        classType.genericTypes() == null ? GatheredImports.empty() : collect(classType.genericTypes()),
        GatheredImports.of(classType)
      );
      case CodePrimitiveType primitiveType -> collect(primitiveType.annotations());
    };
  }

  @Override
  public GatheredImports visitAnnotation(CodeAnnotation codeAnnotation) {
    return join(
      codeAnnotation.type().accept(this),
      collect(codeAnnotation.parameters())
    );
  }

  @Override
  public GatheredImports visitField(CodeField codeField) {
    return join(
      maybeAccept(codeField.initializer()),
      collect(codeField.annotations()),
      codeField.type().accept(this)
    );
  }

  @Override
  public GatheredImports visitExpression(CodeExpression expression) {
    return switch (expression) {

      // Simple expressions do not require any imports.
      case SimpleExpression ignored -> GatheredImports.empty();

      case ConstructorInvocation ctor -> join(
        ctor.type().accept(this),
        collect(ctor.parameters()),
        maybeAccept(ctor.source())
      );

      case FieldAccess field -> maybeAccept(field.source());

      case CastExpression(CodeExpression from, CodeType into) -> join(
        from.accept(this),
        maybeAccept(into)
      );

      case InstanceOfExpr inst -> join(
        inst.classType().accept(this),
        inst.source().accept(this)
      );

      case MethodInvocation inv -> join(
        maybeAccept(inv.source()),
        collect(inv.parameters())
      );

      case MethodReference ref -> ref.source().accept(this);

      case MultilineLambda lamb -> lamb.lambdaBlock().accept(this);

      case SingleLineLambda lamb -> lamb.lambdaExpression().accept(this);

      case AssignExpression(CodeExpression leftSide, CodeExpression rightSide) -> join(
        leftSide.accept(this),
        rightSide.accept(this)
      );

      case ConcatExpression(List<CodeExpression> expressions) -> collect(expressions);

      case UnaryMinusExpression(CodeExpression expr) -> expr.accept(this);

      case NotExpression(CodeExpression contained) -> contained.accept(this);

      case AndExpression(CodeExpression left, CodeExpression right) -> join(
        left.accept(this),
        right.accept(this)
      );

      case OrExpression(CodeExpression left, CodeExpression right) -> join(
        left.accept(this),
        right.accept(this)
      );

      case EqExpression(CodeExpression left, CodeExpression right) -> join(
        left.accept(this),
        right.accept(this)
      );

      case NeqExpression(CodeExpression left, CodeExpression right) -> join(
        left.accept(this),
        right.accept(this)
      );

      default ->
        throw new IllegalArgumentException("Expression of type " + expression.getClass() + " was not handled.");
    };
  }

  @Override
  public GatheredImports visitStatement(CodeStatement statement) {
    return switch (statement) {

      case BlankStatement ignored -> GatheredImports.empty();
      case CommentStatement ignored -> GatheredImports.empty();

      case ExpressionStatement expr -> expr.expression().accept(this);

      case ReturnStatement ret -> maybeAccept(ret.returnExpression());

      case ThrowStatement throwStmt -> throwStmt.throwExpression().accept(this);

      case VariableDeclarationStatement variableDec -> join(
        variableDec.variableType().accept(this),
        maybeAccept(variableDec.assignment())
      );

      case IfStatement ifStmt -> join(
        ifStmt.expression().accept(this),
        ifStmt.ifTrue().accept(this),
        maybeAccept(ifStmt.ifFalse())
      );

      case TryStatement(
        CodeBlock tryBlock, List<TryStatement.CatchStatement> catchStatements, @Nullable CodeBlock finallyBlock
      ) -> join(
        tryBlock.accept(this),
        catchStatements.stream()
          .map(catchStmt -> join(
            collect(catchStmt.exceptionTypes()),
            catchStmt.catchBlock().accept(this)
          ))
          .collect(GatheredImports.collector()),
        maybeAccept(finallyBlock)
      );

      default -> throw new IllegalArgumentException("Statement of type " + statement.getClass() + " was not handled.");
    };
  }

  @Override
  public GatheredImports visitAnnotationParameter(CodeAnnotationParameter annotationParameter) {
    return annotationParameter.value().accept(this);
  }

  @Override
  public GatheredImports visitParameterDefinition(CodeParameterDefinition parameter) {
    return join(
      collect(parameter.annotations()),
      parameter.type().accept(this)
    );
  }

  @Override
  public GatheredImports visitCodeBlock(CodeBlock block) {
    return collect(block.statements());
  }

  @Override
  public GatheredImports visitGenericTypeDefinition(CodeGenericTypeDefinition genericTypeDefinition) {
    return maybeAccept(genericTypeDefinition.enclosure());
  }

  @Override
  public GatheredImports visitGenericEnclosure(GenericEnclosure enclosure) {
    return enclosure.encloses().accept(this);
  }

  @Override
  public GatheredImports visitDocumentation(CodeDocumentation documentation) {
    return switch (documentation) {
      case CodeDocumentation.ClassReference classReference -> classReference.codeClass().accept(this);
      case CodeDocumentation.ClassReferenceMeta classReferenceMeta -> classReferenceMeta.type().accept(this);
      case CodeDocumentation.MethodReference methodReference -> maybeAccept(methodReference.source());
      case CodeDocumentation.MethodReferenceMeta methodReferenceMeta -> maybeAccept(methodReferenceMeta.source());
      //@formatter:off
      case CodeDocumentation.DocumentationComponentList documentationComponentList -> collect(documentationComponentList.components());
      //@formatter:on
      default -> GatheredImports.empty();
    };
  }
}
