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

import net.strokkur.jap.code.classmodel.CodePackage;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.expression.source.MethodReferenceSource;
import net.strokkur.jap.code.type.convert.ConvertToClassType;
import net.strokkur.jap.code.type.convert.ConvertToGenericTypeDefinable;
import net.strokkur.jap.code.type.generics.CodeEnclosable;
import net.strokkur.jap.code.type.generics.CodeGenericTypeDefinable;
import net.strokkur.jap.code.type.impl.CodeClassTypeImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.List;

public sealed interface CodeClassType
  extends CodeType, ConvertToClassType, MethodReferenceSource, CodeEnclosable, Comparable<CodeClassType>
  permits CodeClassTypeImpl {

  //
  // Access.
  //

  String simpleName();

  CodePackage codePackage();

  @Unmodifiable
  @Contract(pure = true)
  @Nullable
  List<CodeGenericTypeDefinable> genericTypes();

  //
  // Modifications.
  //

  /// If this [CodeClassType] references an inner class, this method returns the top level class type.
  CodeClassType toTopLevel();

  @Override
  CodeClassType withAnnotations(List<? extends ConvertToAnnotation> annotations);

  @Override
  default CodeClassType withAnnotations(ConvertToAnnotation... annotations) {
    return withAnnotations(List.of(annotations));
  }

  @Override
  CodeClassType withoutGenerics();

  @Override
  CodeClassType typed(List<? extends ConvertToGenericTypeDefinable> types);

  @Override
  default CodeClassType typed(ConvertToGenericTypeDefinable... types) {
    return typed(List.of(types));
  }

  //
  // Util methods and interface impl.
  //

  /// The value returned from [#name()] may differ from one returned from [#simpleName()]
  /// in that [#name()] only returns the canonical name of the class itself (`TestClass`),
  /// whilst [#simpleName()] returns the full path, including parent classes, if nested (`UpperClass.TestClass`.)
  default String name() {
    return List.of(simpleName().split("\\.")).getLast();
  }

  @Override
  default CodeClassType toClassType() {
    return this;
  }

  @Override
  default String fullyQualifiedName() {
    return codePackage().path() + "." + simpleName();
  }

  /// A name in the format `com.package.name.ParentClass$NestedClass`. This string
  /// is intended to be usable inside [CodeTypes#of(String)].
  default String identifiableName() {
    return codePackage().path() + "." + simpleName().replace('.', '$');
  }

  @Override
  default MethodReferenceSource toMethodReferenceSource() {
    return this;
  }

  @Override
  default CodeClassType toType() {
    return this;
  }

  @Override
  default CodeEnclosable toEnclosable() {
    return this;
  }

  @Override
  default CodeGenericTypeDefinable toGenericTypeDefinable() {
    return this;
  }

  @Override
  default int compareTo(CodeClassType other) {
    return fullyQualifiedName().compareTo(other.fullyQualifiedName());
  }
}
