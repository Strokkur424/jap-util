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
package net.strokkur.jap.code.type;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToGenericType;
import net.strokkur.jap.code.expression.source.MethodReferenceSource;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record CodeClassType(
  CodePackage codePackage,
  String simpleName,
  @Nullable List<CodeGenericType> genericTypes,
  List<CodeAnnotation> annotations
) implements CodeType, ConvertToClassType, MethodReferenceSource, Comparable<CodeClassType> {

  /// The value returned from [#name()] may differ from one returned from [#simpleName()]
  /// in that [#name()] only returns the canonical name of the class itself (`TestClass`),
  /// whilst [#simpleName()] returns the full path, including parent classes, if nested (`UpperClass.TestClass`.)
  public String name() {
    return List.of(simpleName().split("\\.")).getLast();
  }

  @Override
  public CodeClassType toClassType() {
    return this;
  }

  @Override
  public String fullyQualifiedName() {
    return codePackage().path() + "." + simpleName();
  }

  @Override
  public CodeClassType withAnnotations(ConvertToAnnotation... annotations) {
    return new CodeClassType(
      codePackage,
      simpleName,
      genericTypes,
      Arrays.stream(annotations)
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  public CodeClassType toTopLevel() {
    return new CodeClassType(
      codePackage,
      simpleName.split("\\.", 2)[0],
      genericTypes,
      annotations
    );
  }

  @Override
  public CodeClassType withoutGenerics() {
    return new CodeClassType(
      codePackage, simpleName, null, annotations
    );
  }

  /// A name in the format `com.package.name.ParentClass$NestedClass`. This string
  /// is intended to be usable inside [CodeTypes#of(String)].
  public String identifiableName() {
    return codePackage.path() + "." + simpleName.replace('.', '$');
  }

  @Override
  public CodeClassType typed(ConvertToGenericType... genericTypes) {
    return new CodeClassType(
      codePackage,
      simpleName,
      Arrays.stream(genericTypes)
        .map(ConvertToGenericType::toGenericType)
        .toList(),
      annotations
    );
  }

  @Override
  public MethodReferenceSource toMethodReferenceSource() {
    return this;
  }

  @Override
  public CodeClassType toType() {
    return this;
  }

  @Override
  public int compareTo(CodeClassType other) {
    return fullyQualifiedName().compareTo(other.fullyQualifiedName());
  }

  @Override
  public String toString() {
    return identifiableName() + (genericTypes == null ? "" : genericTypes.stream()
      .map(CodeGenericType::toString)
      .collect(Collectors.joining(", ", "<", ">"))
    );
  }
}
