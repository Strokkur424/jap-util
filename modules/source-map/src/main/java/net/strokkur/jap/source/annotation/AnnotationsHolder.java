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
import net.strokkur.jap.source.classmodel.SourceElement;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public interface AnnotationsHolder extends SourceElement {

  int MAX_INHERITANCE_LAYER = 3;

  List<SourceAnnotation> annotations();

  //
  // Single-layer, single-value methods.
  //

  default Optional<SourceAnnotation> findAnnotation(ConvertToClassType type) {
    return annotations().stream()
      .filter(anno -> anno.type().equals(type.toClassType()))
      .findFirst();
  }

  default Optional<SourceAnnotation> findAnnotation(Class<? extends Annotation> annotationClass) {
    return findAnnotation(CodeTypes.ofJavaClass(annotationClass));
  }

  default boolean hasAnnotation(ConvertToClassType type) {
    return findAnnotation(type).isPresent();
  }

  default boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
    return hasAnnotation(CodeTypes.ofJavaClass(annotationClass));
  }

  default SourceAnnotation getAnnotation(ConvertToClassType type) {
    return findAnnotation(type).orElseThrow();
  }

  default SourceAnnotation getAnnotation(Class<? extends Annotation> type) {
    return findAnnotation(type).orElseThrow();
  }

  default <T extends Annotation> Optional<T> findAnnotationValue(Class<T> annotationType) {
    return findAnnotation(annotationType).map(a -> a.value(annotationType));
  }

  default <T extends Annotation> T getAnnotationValue(Class<T> annotationType) {
    return getAnnotation(annotationType).value(annotationType);
  }

  //
  // Multi-layer, single-value methods.
  //

  @ApiStatus.Internal
  default Optional<SourceAnnotation> findAnnotationInherited(ConvertToClassType type, int layer) {
    final Optional<SourceAnnotation> foundOnThisLevel = findAnnotation(type);
    if (foundOnThisLevel.isPresent()) {
      return foundOnThisLevel;
    }

    if (layer > MAX_INHERITANCE_LAYER) {
      return Optional.empty();
    }

    return annotations().stream()
      .flatMap(anno -> anno.source().findAnnotationInherited(type, layer + 1).stream())
      .findFirst();
  }

  default Optional<SourceAnnotation> findAnnotationInherited(ConvertToClassType type) {
    return findAnnotationInherited(type, 1);
  }

  default Optional<SourceAnnotation> findAnnotationInherited(Class<? extends Annotation> annotationClass) {
    return findAnnotationInherited(CodeTypes.ofJavaClass(annotationClass));
  }

  default boolean hasAnnotationInherited(ConvertToClassType type) {
    return findAnnotationInherited(type).isPresent();
  }

  default boolean hasAnnotationInherited(Class<? extends Annotation> annotationClass) {
    return hasAnnotationInherited(CodeTypes.ofJavaClass(annotationClass));
  }

  default SourceAnnotation getAnnotationInherited(ConvertToClassType type) {
    return findAnnotationInherited(type).orElseThrow();
  }

  default SourceAnnotation getAnnotationInherited(Class<? extends Annotation> type) {
    return findAnnotationInherited(type).orElseThrow();
  }

  default <T extends Annotation> Optional<T> findAnnotationValueInherited(Class<T> annotationType) {
    return findAnnotationInherited(annotationType).map(a -> a.value(annotationType));
  }

  default <T extends Annotation> T getAnnotationValueInherited(Class<T> annotationType) {
    return getAnnotationInherited(annotationType).value(annotationType);
  }

  //
  // Single-layer. multi-value methods.
  //

  default <T extends Annotation, P extends Annotation> List<T> getAnnotations(@Nullable Class<P> supertype, Class<T> type) {
    if (supertype == null) {
      return findAnnotationValue(type).map(List::of).orElseGet(List::of);
    }

    return findAnnotation(supertype)
      .map(anno -> {
        try {
          final P many = anno.value(supertype);
          final Method values = supertype.getDeclaredMethod("value");
          return List.of((T[]) values.invoke(many));
        } catch (ReflectiveOperationException ex) {
          throw new RuntimeException("Reflection to value() method failed.", ex);
        }
      })
      .orElseGet(() -> findAnnotationValue(type).map(List::of).orElseGet(List::of));
  }

  default boolean hasAnnotations(@Nullable Class<? extends Annotation> supertype, Class<? extends Annotation> type) {
    return !getAnnotations(supertype, type).isEmpty();
  }
}
