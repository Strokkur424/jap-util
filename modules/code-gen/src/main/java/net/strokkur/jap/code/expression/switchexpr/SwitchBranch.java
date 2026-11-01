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
package net.strokkur.jap.code.expression.switchexpr;

import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public sealed interface SwitchBranch extends CodeVisitable permits SwitchBranch.CaseBranch, DefaultBranchImpl {
  CodeVisitable operation();

  @ApiStatus.Internal
  static CaseBranch caseBranch(Match match, CodeVisitable operation) {
    return new ConditionalBranchImpl(match, operation);
  }

  @ApiStatus.Internal
  static SwitchBranch defaultBranch(CodeVisitable operation) {
    return new DefaultBranchImpl(operation);
  }

  @Override
  default <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitEnhancedSwitchBranch(this);
  }

  sealed interface CaseBranch extends SwitchBranch permits ConditionalBranchImpl {
    CodeVisitable condition();
  }

  sealed interface Match extends CodeVisitable {
    @Override
    default  <R> R accept(CodeVisitor<R> visitor) {
      return visitor.visitMatch(this);
    }
  }

  record MatchExpr(CodeExpression expr) implements Match {

  }

  record MatchType(CodeClassType classType, String name, @Nullable CodeExpression when) implements Match {
  }
}
