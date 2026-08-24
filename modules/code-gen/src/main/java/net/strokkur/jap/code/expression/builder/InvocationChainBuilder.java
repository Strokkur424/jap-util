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

import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.Invocation;
import net.strokkur.jap.code.expression.source.FieldMethodSource;
import net.strokkur.jap.code.util.StyleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class InvocationChainBuilder implements ConvertToExpression, ConvertToStatement {
  private final FieldMethodSource source;
  private final List<Function<FieldMethodSource, Invocation>> chainedMethods = new ArrayList<>();

  public InvocationChainBuilder(FieldMethodSource source) {
    this.source = source;
  }

  public InvocationChainBuilder chainField(String fieldName) {
    chainedMethods.add(source -> source.chainField(fieldName));
    return this;
  }

  public InvocationChainBuilder chainMethod(String name, ConvertToExpression... parameters) {
    chainedMethods.add(source -> source.chainMethod(name, parameters).toMethodInvocation());
    return this;
  }

  public InvocationChainBuilder chainMethod(String name, StyleConfig styleConfig, ConvertToExpression... parameters) {
    chainedMethods.add(source -> source.chainMethod(name, parameters).setStyle(styleConfig).toMethodInvocation());
    return this;
  }

  @Override
  public CodeExpression toExpression() {
    if (chainedMethods.isEmpty()) {
      throw new IllegalStateException("Tried to build MethodInvocationChainBuilder without any methods chained.");
    }

    Invocation curr = chainedMethods.getFirst().apply(source);
    for (int i = 1; i < chainedMethods.size(); i++) {
      curr = chainedMethods.get(i).apply(curr);
    }
    return curr.toExpression();
  }
}
