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
package net.strokkur.jap.code.type.generic;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.convert.ConvertToGenericType;
import net.strokkur.jap.code.type.CodeType;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public record CodeGenericType(
  @Nullable String genericName,
  @Nullable GenericEnclosure enclosure,
  List<CodeAnnotation> annotations
) implements CodeType, ConvertToGenericType {

  public boolean isWildcard() {
    return genericName == null;
  }

  @Override
  public String simpleName() {
    return isWildcard() ? "?" : genericName;
  }

  @Override
  public String fullyQualifiedName() {
    return isWildcard() ? "?" : genericName;
  }

  @Override
  public CodeGenericType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodeGenericType(
      genericName,
      enclosure,
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  @Override
  public CodeGenericType withAnnotations(ConvertToAnnotation... annotations) {
    return withAnnotations(List.of(annotations));
  }

  @Override
  public CodeGenericType toGenericType() {
    return this;
  }

  @Override
  public String toString() {
    return simpleName() + (enclosure == null ? "" : enclosure.toString());
  }
}
