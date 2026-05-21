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

import net.strokkur.jap.code.convert.ConvertToGenericType;
import net.strokkur.jap.code.convert.ConvertToType;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CodeTypes {

  public static CodeArrayType asArray(ConvertToType inner) {
    return new CodeArrayType(inner.toType());
  }

  public static CodeGenericType genericWildcard() {
    return new CodeGenericType(null, null);
  }

  public static CodeGenericType genericWildcardEnclosure(GenericEnclosure enclosure) {
    return new CodeGenericType(null, enclosure);
  }

  public static CodeGenericType generic(String genericTypeName) {
    return new CodeGenericType(genericTypeName, null);
  }

  public static CodeGenericType genericEnclosure(String genericTypeName, GenericEnclosure enclosure) {
    return new CodeGenericType(genericTypeName, enclosure);
  }

  /// A fully qualified name in the form `net.pkg.ClassName$Outer$Inner`.
  public static CodeClassType ofClass(String fullyQualifiedName) {
    return ofClass(fullyQualifiedName, null);
  }

  /// A fully qualified name in the form `net.pkg.ClassName$Outer$Inner`.
  public static CodeClassType ofClassTyped(String fullyQualifiedName, ConvertToGenericType... types) {
    return ofClass(fullyQualifiedName, Arrays.stream(types)
      .map(ConvertToGenericType::toGenericType)
      .toList());
  }

  private static CodeClassType ofClass(String fullyQualifiedName, @Nullable List<CodeGenericType> types) {
    final List<String> splitInner = List.of(fullyQualifiedName.split("\\$"));
    final List<String> splitPackage = List.of(splitInner.getFirst().split("\\."));

    final List<String> namePath = new ArrayList<>(List.of(splitPackage.getLast()));
    namePath.addAll(splitInner.subList(1, splitInner.size()));

    return new CodeClassType(
      CodePackage.of(splitPackage.subList(0, splitPackage.size() - 1)),
      String.join(".", namePath),
      types
    );
  }

  private CodeTypes() throws IllegalAccessError {
    throw new IllegalAccessError("A util class' constructor should not be called.");
  }
}
