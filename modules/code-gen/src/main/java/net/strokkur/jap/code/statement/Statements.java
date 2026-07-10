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
package net.strokkur.jap.code.statement;

import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.convert.ConvertToType;
import org.jspecify.annotations.Nullable;

public final class Statements {

  public static ExpressionStatement expressionStatement(ConvertToExpression expression) {
    return new ExpressionStatement(expression.toExpression());
  }

  public static CodeStatement variableDeclaration(ConvertToType type, String name, @Nullable ConvertToExpression assignment) {
    return new VariableDeclarationStatement(type.toType(), name, assignment == null ? null : assignment.toExpression(), false);
  }

  public static CodeStatement variableDeclarationFinal(ConvertToType type, String name, ConvertToExpression assignment) {
    return new VariableDeclarationStatement(type.toType(), name, assignment.toExpression(), true);
  }

  public static CodeStatement returnStmt() {
    return returnStmt(null);
  }

  public static CodeStatement returnStmt(@Nullable ConvertToExpression returnExpression) {
    return new ReturnStatement(returnExpression == null ? null : returnExpression.toExpression());
  }

  public static CodeStatement throwStatement(ConvertToExpression throwExpression) {
    return new ThrowStatement(throwExpression.toExpression());
  }

  public static CodeStatement blank() {
    return BlankStatement.INSTANCE;
  }

  public static IfStatement ifStmt(ConvertToExpression booleanExpr, ConvertToStatement... ifTrue) {
    return ifStmt(booleanExpr, CodeBlock.of(ifTrue), null);
  }

  public static IfStatement ifStmt(ConvertToExpression booleanExpr, CodeBlock ifTrue, @Nullable CodeBlock ifFalse) {
    return new IfStatement(
      booleanExpr.toExpression(),
      ifTrue,
      ifFalse
    );
  }

  private Statements() throws IllegalAccessError {
    throw new IllegalAccessError("This utility class cannot be instantiated.");
  }
}
