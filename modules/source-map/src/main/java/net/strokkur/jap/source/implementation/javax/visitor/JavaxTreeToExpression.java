/*
 * This file is part of source-map, licensed under the MIT License.
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
package net.strokkur.jap.source.implementation.javax.visitor;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SimpleTreeVisitor;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.expression.MethodInvocation;

/// A very incomplete mapper from [com.sun.source.tree.Tree] to [CodeExpression].
public class JavaxTreeToExpression extends SimpleTreeVisitor<CodeExpression, Void> {
  private boolean nextIdentifierIsMethodInvocation = false;

  @Override
  public CodeExpression visitVariable(VariableTree node, Void unused) {
    return Expressions.variable(node.getName().toString());
  }

  @Override
  public CodeExpression visitIdentifier(IdentifierTree node, Void unused) {
    if (nextIdentifierIsMethodInvocation) {
      return Expressions.methodInvocation(node.getName().toString()).toMethodInvocation();
    } else {
      return Expressions.variable(node.getName().toString());
    }
  }

  @Override
  public CodeExpression visitMethodInvocation(MethodInvocationTree node, Void unused) {
    nextIdentifierIsMethodInvocation = true;
    return ((MethodInvocation) node.getMethodSelect().accept(this, unused)).builder()
      .addParameters(node.getArguments().stream()
        .map(tree -> tree.accept(this, unused))
        .toArray(CodeExpression[]::new))
      .toMethodInvocation();
  }

  @Override
  public CodeExpression visitLiteral(LiteralTree node, Void unused) {
    if (node.getValue() == null) {
      return Expressions.nullExpr();
    }
    if (node.getValue() instanceof String str) {
      return Expressions.string(str);
    }

    return switch (node.getKind()) {
      case INT_LITERAL -> Expressions.intExpr((int) node.getValue());
      case LONG_LITERAL -> Expressions.longExpr((long) node.getValue());
      case FLOAT_LITERAL -> Expressions.floatExpr((float) node.getValue());
      case DOUBLE_LITERAL -> Expressions.doubleExpr((double) node.getValue());
      case BOOLEAN_LITERAL -> Expressions.bool((boolean) node.getValue());
      default -> Expressions.string("Unknown type literal (" + node.getValue() + " // " + node.getKind() + ")");
    };
  }
}
