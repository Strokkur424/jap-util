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
package net.strokkur.jap.code.expression.simple;

import net.strokkur.jap.code.expression.BooleanExpression;

public record CodeBooleanExpression(boolean value) implements BooleanExpression {
  @Override
  public boolean isInverted() {
    // A boolean constant is never inverted by putting a ! in front of it, you just switch out
    // a `true` with a `false`, hence #invert() just replaces the boolean value outright.
    return false;
  }

  @Override
  public BooleanExpression invert() {
    return new CodeBooleanExpression(!value());
  }
}
