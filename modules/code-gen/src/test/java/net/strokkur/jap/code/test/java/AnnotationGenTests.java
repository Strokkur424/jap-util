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
package net.strokkur.jap.code.test.java;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.annotations.CodeAnnotationParameter;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import org.junit.jupiter.api.Test;

import java.util.Set;

class AnnotationGenTests extends AbstractGenTest {

  @Test
  void test() {
    check(Set.of(JSpecifyTypes.NULL_MARKED), "@NullMarked", CodeAnnotation.of(JSpecifyTypes.NULL_MARKED));
    check(Set.of(JSpecifyTypes.NULL_MARKED), "@NullMarked(5)", CodeAnnotation.of(JSpecifyTypes.NULL_MARKED, Expressions.intExpr(5)));
    check(Set.of(
        JSpecifyTypes.NULL_MARKED),
      "@NullMarked(5)",
      CodeAnnotation.of(
        JSpecifyTypes.NULL_MARKED,
        CodeAnnotationParameter.of("value", Expressions.intExpr(5))
      )
    );
    check(Set.of(
        JSpecifyTypes.NULL_MARKED),
      "@NullMarked(value = 5, id = 1)",
      CodeAnnotation.of(
        JSpecifyTypes.NULL_MARKED,
        CodeAnnotationParameter.of("value", Expressions.intExpr(5)),
        CodeAnnotationParameter.of("id", Expressions.intExpr(1))
      )
    );
  }
}
