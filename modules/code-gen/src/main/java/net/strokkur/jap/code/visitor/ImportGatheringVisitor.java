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
package net.strokkur.jap.code.visitor;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.annotations.CodeAnnotationParameter;
import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.expression.AssignExpression;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.ConstructorInvocation;
import net.strokkur.jap.code.expression.FieldAccess;
import net.strokkur.jap.code.expression.InstanceOfExpr;
import net.strokkur.jap.code.expression.MethodInvocation;
import net.strokkur.jap.code.expression.MethodReference;
import net.strokkur.jap.code.expression.MultilineLambda;
import net.strokkur.jap.code.expression.SingleLineLambda;
import net.strokkur.jap.code.expression.UnaryMinusExpression;
import net.strokkur.jap.code.expression.bool.AndExpression;
import net.strokkur.jap.code.expression.bool.NotExpression;
import net.strokkur.jap.code.expression.bool.OrExpression;
import net.strokkur.jap.code.expression.simple.SimpleExpression;
import net.strokkur.jap.code.statement.BlankStatement;
import net.strokkur.jap.code.statement.CodeStatement;
import net.strokkur.jap.code.statement.ExpressionStatement;
import net.strokkur.jap.code.statement.IfStatement;
import net.strokkur.jap.code.statement.ReturnStatement;
import net.strokkur.jap.code.statement.ThrowStatement;
import net.strokkur.jap.code.statement.VariableDeclarationStatement;
import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportGatheringVisitor implements CodeVisitor<Set<CodeClassType>> {

  @SafeVarargs
  private Set<CodeClassType> join(Set<CodeClassType>... all) {
    return Stream.of(all)
      .flatMap(Collection::stream)
      .collect(Collectors.toSet());
  }

  private Set<CodeClassType> maybeAccept(@Nullable CodeVisitable visitable) {
    if (visitable != null) {
      return visitable.accept(this);
    } else {
      return Set.of();
    }
  }

  private <S extends CodeVisitable> Set<CodeClassType> collect(Collection<S> collection) {
    return collection.stream()
      .flatMap(visitable -> visitable.accept(this).stream())
      .collect(Collectors.toSet());
  }

  @Override
  public Set<CodeClassType> visitClass(CodeClass codeClass) {
    return join(
      Set.of(codeClass.classType()),
      collect(codeClass.methods()),
      collect(codeClass.constructors()),
      maybeAccept(codeClass.extendsType()),
      collect(codeClass.implementsTypes()),
      collect(codeClass.fields()),
      collect(codeClass.annotations()),
      collect(codeClass.genericTypes())
    );
  }

  @Override
  public Set<CodeClassType> visitMethod(CodeMethod codeMethod) {
    return join(
      collect(codeMethod.parameters()),
      collect(codeMethod.throwsExceptions()),
      codeMethod.returnType().accept(this),
      collect(codeMethod.codeBlock().statements()),
      collect(codeMethod.generics()),
      collect(codeMethod.annotations())
    );
  }

  @Override
  public Set<CodeClassType> visitConstructor(CodeConstructor ctor) {
    return join(
      collect(ctor.annotations()),
      collect(ctor.generics()),
      collect(ctor.throwsExceptions()),
      collect(ctor.parameters()),
      ctor.codeBlock().accept(this)
    );
  }

  @Override
  public Set<CodeClassType> visitType(CodeType codeType) {
    return switch (codeType) {
      case CodeArrayType(CodeType inner) -> inner.accept(this);
      case CodeGenericType(String genericName, GenericEnclosure enclosure) -> maybeAccept(enclosure);
      case CodeClassType codeClass -> join(
        codeClass.genericTypes() == null ? Set.of() : collect(codeClass.genericTypes()),
        Set.of(codeClass)
      );
      default -> Set.of();
    };
  }

  @Override
  public Set<CodeClassType> visitAnnotation(CodeAnnotation codeAnnotation) {
    return join(
      Set.of(codeAnnotation.type()),
      collect(codeAnnotation.parameters())
    );
  }

  @Override
  public Set<CodeClassType> visitField(CodeField codeField) {
    return join(
      maybeAccept(codeField.initializer()),
      collect(codeField.annotations()),
      codeField.type().accept(this)
    );
  }

  @Override
  public Set<CodeClassType> visitExpression(CodeExpression expression) {
    return switch (expression) {

      // Simple expressions do not require any imports.
      case SimpleExpression ignored -> Set.of();

      case ConstructorInvocation ctor -> join(
        Set.of(ctor.type()),
        collect(ctor.parameters()),
        maybeAccept(ctor.source())
      );

      case FieldAccess field -> maybeAccept(field.source());

      case InstanceOfExpr inst -> join(
        Set.of(inst.classType()),
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

      default ->
        throw new IllegalArgumentException("Expression of type " + expression.getClass() + " was not handled.");
    };
  }

  @Override
  public Set<CodeClassType> visitStatement(CodeStatement statement) {
    return switch (statement) {

      case BlankStatement ignored -> Set.of();

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

      default -> throw new IllegalArgumentException("Statement of type " + statement.getClass() + " was not handled.");
    };
  }

  @Override
  public Set<CodeClassType> visitAnnotationParameter(CodeAnnotationParameter annotationParameter) {
    return annotationParameter.value().accept(this);
  }

  @Override
  public Set<CodeClassType> visitParameterDefinition(CodeParameterDefinition parameter) {
    return join(
      collect(parameter.annotations()),
      parameter.type().accept(this)
    );
  }

  @Override
  public Set<CodeClassType> visitCodeBlock(CodeBlock block) {
    return collect(block.statements());
  }

  @Override
  public Set<CodeClassType> visitGenericTypeDefinition(CodeGenericTypeDefinition genericTypeDefinition) {
    return maybeAccept(genericTypeDefinition.enclosure());
  }

  @Override
  public Set<CodeClassType> visitGenericEnclosure(GenericEnclosure enclosure) {
    return enclosure.encloses().accept(this);
  }
}
