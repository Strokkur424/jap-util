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
package net.strokkur.jap.source.annotation;

import net.strokkur.jap.code.annotations.CodeAnnotationParameterImpl;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.type.CodeClassType;

public final class SourceAnnotationParameter extends CodeAnnotationParameterImpl {
  private final Object value;
  private final ConvertToExpression expression;

  public SourceAnnotationParameter(
    String name,
    Object value,
    ConvertToExpression expression
  ) {
    //noinspection DataFlowIssue
    super(name, null);
    this.value = value;
    this.expression = expression;
  }

  public CodeClassType classValue() {
    if (value instanceof CodeClassType type) {
      return type;
    }
    throw new IllegalArgumentException("Expected Class, found " + value.getClass());
  }

  public Object value() {
    return value;
  }

  @Override
  public CodeExpression expression() {
    return expression.toExpression();
  }
}
