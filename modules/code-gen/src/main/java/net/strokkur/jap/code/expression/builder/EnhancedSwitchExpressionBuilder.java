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
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.EnhancedSwitchExpression;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class EnhancedSwitchExpressionBuilder implements ConvertToExpression {
  private @Nullable CodeExpression head = null;
  private final List<EnhancedSwitchExpression.Branch> branches = new ArrayList<>();

  private EnhancedSwitchExpression.@Nullable Branch defaultBranch = null;
  private EnhancedSwitchExpression.@Nullable Branch nullBranch = null;

  public EnhancedSwitchExpressionBuilder head(ConvertToExpression head) {
    this.head = head.toExpression();
    return this;
  }

  public EnhancedSwitchExpressionBuilder caseExpr(ConvertToExpression cond, ConvertToExpression expr) {
    this.branches.add(new EnhancedSwitchExpression.CaseExpressionBranch(cond.toExpression(), expr.toExpression()));
    return this;
  }

  public EnhancedSwitchExpressionBuilder caseBlock(ConvertToExpression cond, ConvertToStatement... statements) {
    this.branches.add(new EnhancedSwitchExpression.CaseBlockBranch(cond.toExpression(), CodeBlock.of(statements)));
    return this;
  }

  public EnhancedSwitchExpressionBuilder defaultExpr(ConvertToExpression expr) {
    this.defaultBranch = new EnhancedSwitchExpression.DefaultExpressionBranch(expr.toExpression());
    return this;
  }

  public EnhancedSwitchExpressionBuilder defaultBlock(ConvertToStatement... statements) {
    this.defaultBranch = new EnhancedSwitchExpression.DefaultBlockBranch(CodeBlock.of(statements));
    return this;
  }

  public EnhancedSwitchExpressionBuilder nullExpr(ConvertToExpression expr) {
    this.nullBranch = new EnhancedSwitchExpression.NullExpressionBranch(expr.toExpression());
    return this;
  }

  public EnhancedSwitchExpressionBuilder nullBlock(ConvertToStatement... statements) {
    this.nullBranch = new EnhancedSwitchExpression.NullBlockBranch(CodeBlock.of(statements));
    return this;
  }

  public EnhancedSwitchExpression toEnhancedSwitch() {
    final List<EnhancedSwitchExpression.Branch> allBranches = new ArrayList<>(branches);
    if (nullBranch != null) {
      allBranches.add(nullBranch);
    }
    if (defaultBranch != null) {
      allBranches.add(defaultBranch);
    }

    return new EnhancedSwitchExpression(
      Objects.requireNonNull(head, "no switch head provided"),
      Collections.unmodifiableList(allBranches)
    );
  }

  @Override
  public CodeExpression toExpression() {
    return toEnhancedSwitch();
  }
}
