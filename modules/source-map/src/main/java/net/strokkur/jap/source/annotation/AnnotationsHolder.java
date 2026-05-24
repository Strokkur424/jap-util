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

import net.strokkur.jap.code.convert.ConvertToClassType;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface AnnotationsHolder {

  List<SourceAnnotation> annotations();

  @ApiStatus.Internal
  default Predicate<SourceAnnotation> predicateAnnotationWithType(ConvertToClassType type) {
    return anno -> anno.type().equals(type.toClassType());
  }

  default boolean hasAnnotation(ConvertToClassType type) {
    return annotations().stream()
      .anyMatch(predicateAnnotationWithType(type));
  }

  default List<SourceAnnotation> annotationsByType(ConvertToClassType type) {
    return annotations().stream()
      .filter(predicateAnnotationWithType(type))
      .toList();
  }

  default SourceAnnotation firstAnnotationByType(ConvertToClassType type) {
    return annotations().stream()
      .filter(predicateAnnotationWithType(type))
      .findFirst().orElseThrow();
  }

  default boolean hasAnnotationInherited(ConvertToClassType type) {
    if (hasAnnotation(type)) {
      return true;
    }

    return annotations().stream().anyMatch(anno -> anno.source().hasAnnotationInherited(type));
  }

  default SourceAnnotation firstAnnotationInherited(ConvertToClassType type) {
    if (hasAnnotation(type)) {
      return firstAnnotationByType(type);
    }

    return annotations().stream()
      .filter(anno -> anno.source().hasAnnotationInherited(type))
      .map(anno -> anno.source().firstAnnotationInherited(type))
      .findFirst().orElseThrow();
  }

  default List<SourceAnnotation> annotationsInherited(ConvertToClassType type) {
    return annotations().stream()
      .flatMap(annotation -> annotation.type().equals(type.toClassType())
        ? Stream.of(annotation)
        : annotation.source().annotationsInherited(type).stream())
      .toList();
  }
}
