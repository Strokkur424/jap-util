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
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.source.classmodel.SourceAnnotationInterface;
import net.strokkur.jap.source.classmodel.SourceElement;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface AnnotationsHolder extends SourceElement {

  List<SourceAnnotation> annotations();

  @ApiStatus.Internal
  default Predicate<SourceAnnotation> predicateAnnotationWithType(ConvertToClassType type) {
    return anno -> anno.type().equals(type.toClassType());
  }

  default boolean hasAnnotation(ConvertToClassType type) {
    return annotations().stream()
      .anyMatch(predicateAnnotationWithType(type));
  }

  default boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
    return hasAnnotation(CodeTypes.ofJavaClass(annotationClass));
  }

  default List<SourceAnnotation> annotationsByType(ConvertToClassType type) {
    return annotations().stream()
      .filter(predicateAnnotationWithType(type))
      .toList();
  }

  default <T extends Annotation> List<T> annotationsValuesByType(Class<T> annotationClass) {
    return annotationsByType(annotationClass).stream()
      .map(a -> a.value(annotationClass))
      .toList();
  }

  default List<SourceAnnotation> annotationsByType(Class<? extends Annotation> annotationClass) {
    return annotationsByType(CodeTypes.ofJavaClass(annotationClass));
  }

  default SourceAnnotation firstAnnotationByType(ConvertToClassType type) {
    return firstAnnotationByTypeOptional(type).orElseThrow();
  }

  default <T extends Annotation> T firstAnnotationValueByType(Class<T> annotationClass) {
    return firstAnnotationByType(annotationClass).value(annotationClass);
  }

  default SourceAnnotation firstAnnotationByType(Class<? extends Annotation> annotationClass) {
    return firstAnnotationByType(CodeTypes.ofJavaClass(annotationClass));
  }

  default Optional<SourceAnnotation> firstAnnotationByTypeOptional(ConvertToClassType type) {
    return annotations().stream()
      .filter(predicateAnnotationWithType(type))
      .findFirst();
  }

  default <T extends Annotation> Optional<T> firstAnnotationValueByTypeOptional(Class<T> annotationClass) {
    return firstAnnotationByTypeOptional(annotationClass)
      .map(a -> a.value(annotationClass));
  }

  default Optional<SourceAnnotation> firstAnnotationByTypeOptional(Class<? extends Annotation> annotationClass) {
    return firstAnnotationByTypeOptional(CodeTypes.ofJavaClass(annotationClass));
  }

  default boolean hasAnnotationInherited(ConvertToClassType type) {
    if (hasAnnotation(type)) {
      return true;
    }

    class Internal {
      static final int MAX_DEPTH = 3;

      boolean recurseSearching(SourceAnnotationInterface annotation, int depth) {
        if (annotation.hasAnnotation(type)) {
          return true;
        }

        if (depth > MAX_DEPTH) {
          return false;
        }

        return annotation.annotations().stream()
          .anyMatch(anno -> recurseSearching(anno.source(), depth + 1));
      }
    }

    final Internal internal = new Internal();
    return annotations().stream()
      .anyMatch(anno -> internal.recurseSearching(anno.source(), 1));
  }

  default boolean hasAnnotationInherited(Class<? extends Annotation> annotationClass) {
    return hasAnnotationInherited(CodeTypes.ofJavaClass(annotationClass));
  }

  default SourceAnnotation firstAnnotationInherited(ConvertToClassType type) {
    return firstAnnotationInheritedOptional(type).orElseThrow();
  }

  default Optional<SourceAnnotation> firstAnnotationInheritedOptional(ConvertToClassType type) {
    if (hasAnnotation(type)) {
      return firstAnnotationByTypeOptional(type);
    }

    return annotations().stream()
      .filter(anno -> anno.source().hasAnnotationInherited(type))
      .map(anno -> anno.source().firstAnnotationInherited(type))
      .findFirst();
  }

  default SourceAnnotation firstAnnotationInherited(Class<? extends Annotation> annotationClass) {
    return firstAnnotationInherited(CodeTypes.ofJavaClass(annotationClass));
  }

  default Optional<SourceAnnotation> firstAnnotationInheritedOptional(Class<? extends Annotation> annotationClass) {
    return firstAnnotationInheritedOptional(CodeTypes.ofJavaClass(annotationClass));
  }

  default <T extends Annotation> T firstAnnotationValueInherited(Class<T> annotationClass) {
    return firstAnnotationInherited(CodeTypes.ofJavaClass(annotationClass)).value(annotationClass);
  }

  default <T extends Annotation> Optional<T> firstAnnotationValueInheritedOptional(Class<T> annotationClass) {
    return firstAnnotationInheritedOptional(CodeTypes.ofJavaClass(annotationClass))
      .map(a -> a.value(annotationClass));
  }

  default List<SourceAnnotation> annotationsInherited(ConvertToClassType type) {
    class Internal {
      static final int MAX_DEPTH = 3;

      Stream<SourceAnnotation> annotationsInherited(SourceAnnotationInterface anno, int depth) {
        if (depth >= MAX_DEPTH) {
          return Stream.empty();
        }

        return anno.annotations().stream()
          .flatMap(annotation -> annotation.type().equals(type.toClassType())
            ? Stream.of(annotation)
            : annotationsInherited(annotation.source(), depth + 1)
          );
      }
    }

    final Internal internal = new Internal();
    return annotations().stream()
      .flatMap(annotation -> annotation.type().equals(type.toClassType())
        ? Stream.of(annotation)
        : internal.annotationsInherited(annotation.source(), 1))
      .toList();
  }

  default <T extends Annotation> List<T> annotationsValuesInherited(Class<T> annotationClass) {
    return annotationsInherited(annotationClass).stream()
      .map(a -> a.value(annotationClass))
      .toList();
  }

  default List<SourceAnnotation> annotationsInherited(Class<? extends Annotation> annotationClass) {
    return annotationsInherited(CodeTypes.ofJavaClass(annotationClass));
  }
}
