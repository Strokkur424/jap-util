/*
 * This file is part of code-gen, licensed under the MIT License.
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
package net.strokkur.jap.code.expression;

import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToMethodReferenceSource;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.expression.builder.ConstructorInvocationBuilder;
import net.strokkur.jap.code.expression.builder.MethodInvocationBuilder;
import net.strokkur.jap.code.expression.simple.CodeBooleanExpression;
import net.strokkur.jap.code.expression.simple.CodeDoubleExpression;
import net.strokkur.jap.code.expression.simple.CodeFloatExpression;
import net.strokkur.jap.code.expression.simple.CodeIntExpression;
import net.strokkur.jap.code.expression.simple.CodeLongExpression;
import net.strokkur.jap.code.expression.simple.CodeNullExpression;
import net.strokkur.jap.code.expression.simple.CodeStringExpression;
import net.strokkur.jap.code.expression.simple.CodeVariableExpression;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class Expressions {

  public static CodeBooleanExpression bool(boolean value) {
    return new CodeBooleanExpression(value);
  }

  public static CodeDoubleExpression doubleExpr(double value) {
    return new CodeDoubleExpression(value);
  }

  public static CodeFloatExpression floatExpr(float value) {
    return new CodeFloatExpression(value);
  }

  public static CodeIntExpression intExpr(int value) {
    return new CodeIntExpression(value);
  }

  public static CodeLongExpression longExpr(long value) {
    return new CodeLongExpression(value);
  }

  public static CodeNullExpression nullExpr() {
    return CodeNullExpression.NULL;
  }

  public static CodeStringExpression string(String value) {
    return new CodeStringExpression(value);
  }

  public static CodeVariableExpression thisExpr() {
    return new CodeVariableExpression("this");
  }

  public static CodeVariableExpression variable(String name) {
    return new CodeVariableExpression(name);
  }

  public static MethodInvocationBuilder methodInvocation(String methodName) {
    return new MethodInvocationBuilder()
      .setName(methodName);
  }

  public static UnaryMinusExpression unaryMinus(ConvertToExpression expr) {
    return new UnaryMinusExpression(expr.toExpression());
  }

  public static ConstructorInvocationBuilder ctorInvocation(ConvertToClassType classType) {
    return new ConstructorInvocationBuilder(classType.toClassType());
  }

  public static MethodReference methodReference(ConvertToMethodReferenceSource source, String name) {
    return new MethodReference(source.toMethodReferenceSource(), name);
  }

  public static FieldAccess fieldAccess(String fieldName) {
    return new FieldAccess(null, fieldName);
  }

  public static AssignExpression assign(ConvertToExpression left, ConvertToExpression right) {
    return new AssignExpression(left.toExpression(), right.toExpression());
  }

  public static InstanceOfExpr instanceOf(ConvertToExpression source, ConvertToClassType classType) {
    return instanceOf(source, classType, null);
  }

  public static InstanceOfExpr instanceOf(ConvertToExpression source, ConvertToClassType classType, @Nullable String targetVariable) {
    return new InstanceOfExpr(source.toExpression(), classType.toClassType(), targetVariable, false);
  }

  public static SingleLineLambda lambdaInline(ConvertToExpression lambdaExpression) {
    return lambdaInline(List.of(), lambdaExpression);
  }

  public static SingleLineLambda lambdaInline(String lambdaParameter, ConvertToExpression lambdaExpression) {
    return lambdaInline(List.of(lambdaParameter), lambdaExpression);
  }

  public static SingleLineLambda lambdaInline(List<String> lambdaParameters, ConvertToExpression lambdaExpression) {
    return new SingleLineLambda(lambdaParameters, lambdaExpression.toExpression());
  }

  public static MultilineLambda lambda(ConvertToStatement... lambdaStatements) {
    return lambda(List.of(), lambdaStatements);
  }

  public static MultilineLambda lambda(String lambdaParameter, ConvertToStatement... lambdaStatements) {
    return lambda(List.of(lambdaParameter), lambdaStatements);
  }

  public static MultilineLambda lambda(List<String> lambdaParameters, ConvertToStatement... lambdaStatements) {
    return new MultilineLambda(lambdaParameters, CodeBlock.of(lambdaStatements));
  }

  public static MultilineLambda lambda(CodeBlock lambdaBlock) {
    return lambda(List.of(), lambdaBlock);
  }

  public static MultilineLambda lambda(String lambdaParameter, CodeBlock lambdaBlock) {
    return lambda(List.of(lambdaParameter), lambdaBlock);
  }

  public static MultilineLambda lambda(List<String> lambdaParameters, CodeBlock lambdaBlock) {
    return new MultilineLambda(lambdaParameters, lambdaBlock);
  }

  private Expressions() throws IllegalAccessError {
    throw new IllegalAccessError("You cannot instantiate this class.");
  }
}
