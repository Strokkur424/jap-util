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
package net.strokkur.jap.code.test;

import net.strokkur.jap.code.annotations.CodeAnnotated;
import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodeAnnotatedTests {

  @Test
  void test() {
    final CodeAnnotated holder = new AnnotationHolder(List.of(
      CodeAnnotation.of(JSpecifyTypes.NULL_MARKED),
      CodeAnnotation.of(JSpecifyTypes.NULL_MARKED),
      CodeAnnotation.of(JSpecifyTypes.NULLABLE)
    ));

    final List<CodeAnnotation> nullMarkedAnnotations = holder.annotationsType(JSpecifyTypes.NULL_MARKED);
    assertEquals(2, nullMarkedAnnotations.size());
    assertTrue(nullMarkedAnnotations.stream().allMatch(anno -> anno.type().equals(JSpecifyTypes.NULL_MARKED.toClassType())));

    final List<CodeAnnotation> nullableAnnotations = holder.annotationsType(JSpecifyTypes.NULLABLE);
    assertEquals(1, nullableAnnotations.size());
    assertTrue(nullableAnnotations.stream().allMatch(anno -> anno.type().equals(JSpecifyTypes.NULLABLE.toClassType())));

    final List<CodeAnnotation> nonNullAnnotations = holder.annotationsType(JSpecifyTypes.NON_NULL);
    assertEquals(0, nonNullAnnotations.size());

    assertEquals(holder.firstAnnotationType(JSpecifyTypes.NULL_MARKED).type(), JSpecifyTypes.NULL_MARKED.toClassType());

    assertTrue(holder.hasAnnotations());
    assertTrue(holder.hasAnnotationsType(JSpecifyTypes.NULL_MARKED));
    assertTrue(holder.hasAnnotationsType(JSpecifyTypes.NULLABLE));
    assertFalse(holder.hasAnnotationsType(JSpecifyTypes.NON_NULL));
  }

  private record AnnotationHolder(List<CodeAnnotation> annotations) implements CodeAnnotated {}
}
