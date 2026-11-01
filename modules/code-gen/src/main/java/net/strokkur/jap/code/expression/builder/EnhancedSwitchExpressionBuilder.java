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
package net.strokkur.jap.code.expression.builder;

import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.switchexpr.EnhancedSwitchExpression;
import net.strokkur.jap.code.expression.switchexpr.SwitchBranch;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EnhancedSwitchExpressionBuilder implements ConvertToExpression {
  private @Nullable CodeExpression head = null;
  private final List<SwitchBranch.CaseBranch> branches = new ArrayList<>();
  private @Nullable SwitchBranch defaultBranch = null;

  public EnhancedSwitchExpressionBuilder head(ConvertToExpression head) {
    this.head = head.toExpression();
    return this;
  }

  public EnhancedSwitchExpressionBuilder branchInline(
    ConvertToClassType classType,
    String name,
    ConvertToExpression expr
  ) {
    branches.add(SwitchBranch.caseBranch(
      new SwitchBranch.MatchType(classType.toClassType(), name, null),
      expr.toExpression()
    ));
    return this;
  }

  public EnhancedSwitchExpressionBuilder branchInline(
    ConvertToClassType classType,
    String name,
    ConvertToExpression when,
    ConvertToExpression expr
  ) {
    branches.add(SwitchBranch.caseBranch(
      new SwitchBranch.MatchType(classType.toClassType(), name, when.toExpression()),
      expr.toExpression()
    ));
    return this;
  }

  public EnhancedSwitchExpressionBuilder branchInline(
    ConvertToExpression expression,
    ConvertToExpression expr
  ) {
    branches.add(SwitchBranch.caseBranch(
      new SwitchBranch.MatchExpr(expression.toExpression()),
      expr.toExpression()
    ));
    return this;
  }

  public EnhancedSwitchExpressionBuilder branchBlock(
    ConvertToClassType classType,
    String name,
    ConvertToStatement... statements
  ) {
    branches.add(SwitchBranch.caseBranch(
      new SwitchBranch.MatchType(classType.toClassType(), name, null),
      CodeBlock.of(statements)
    ));
    return this;
  }

  public EnhancedSwitchExpressionBuilder branchBlock(
    ConvertToClassType classType,
    String name,
    ConvertToExpression when,
    ConvertToStatement... statements
  ) {
    branches.add(SwitchBranch.caseBranch(
      new SwitchBranch.MatchType(classType.toClassType(), name, when.toExpression()),
      CodeBlock.of(statements)
    ));
    return this;
  }

  public EnhancedSwitchExpressionBuilder branchBlock(
    ConvertToExpression expression,
    ConvertToStatement... statements
  ) {
    branches.add(SwitchBranch.caseBranch(
      new SwitchBranch.MatchExpr(expression.toExpression()),
      CodeBlock.of(statements)
    ));
    return this;
  }

  public EnhancedSwitchExpressionBuilder defaultInline(ConvertToExpression operation) {
    defaultBranch = SwitchBranch.defaultBranch(operation.toExpression());
    return this;
  }

  public EnhancedSwitchExpressionBuilder defaultBlock(ConvertToStatement... statements) {
    defaultBranch = SwitchBranch.defaultBranch(CodeBlock.of(statements));
    return this;
  }

  public EnhancedSwitchExpression toEnhancedSwitch() {
    return new EnhancedSwitchExpression(
      Objects.requireNonNull(head, "no switch head provided"),
      List.copyOf(branches),
      defaultBranch
    );
  }

  @Override
  public CodeExpression toExpression() {
    return toEnhancedSwitch();
  }
}
