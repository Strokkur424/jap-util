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
import net.strokkur.jap.code.convert.ConvertToFieldMethodSource;
import net.strokkur.jap.code.convert.ConvertToMethodInvocation;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.MethodInvocation;
import net.strokkur.jap.code.expression.source.FieldMethodSource;
import net.strokkur.jap.code.util.StyleConfig;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MethodInvocationBuilder implements ConvertToMethodInvocation {
  private @Nullable String name;
  private @Nullable FieldMethodSource source;
  private final List<CodeExpression> parameters = new ArrayList<>();
  private StyleConfig style = StyleConfig.DEFAULT;

  public MethodInvocationBuilder setName(@Nullable String name) {
    this.name = name;
    return this;
  }

  public MethodInvocationBuilder setSource(@Nullable ConvertToFieldMethodSource source) {
    this.source = source == null ? null : source.toFieldMethodSource();
    return this;
  }

  public MethodInvocationBuilder setStyle(StyleConfig style) {
    this.style = style;
    return this;
  }

  public MethodInvocationBuilder addParameters(ConvertToExpression... parameters) {
    for (ConvertToExpression parameter : parameters) {
      this.parameters.add(parameter.toExpression());
    }
    return this;
  }

  @Override
  public MethodInvocation toMethodInvocation() {
    return new MethodInvocation(
      Objects.requireNonNull(name),
      source,
      List.copyOf(parameters),
      style
    );
  }

  @Override
  public CodeExpression toExpression() {
    return toMethodInvocation();
  }
}
