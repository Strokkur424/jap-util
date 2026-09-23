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

import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.expression.source.FieldMethodSource;
import net.strokkur.jap.code.type.impl.CodePrimitiveTypeImpl;
import net.strokkur.jap.code.type.impl.CodeTypeImpl;

import java.util.List;

public sealed interface CodePrimitiveType
  extends CodeType, FieldMethodSource
  permits CodePrimitiveTypeImpl {

  /// This type cannot be used anywhere else except the return value of a method.
  /// Nothing specifically enforces this, but the JVM does not allow otherwise.
  CodePrimitiveType VOID = CodeTypeImpl.createPrimitive("void", "Void", List.of());

  CodePrimitiveType BYTE = CodeTypeImpl.createPrimitive("byte", "Byte", List.of());
  CodePrimitiveType CHAR = CodeTypeImpl.createPrimitive("char", "Character", List.of());
  CodePrimitiveType SHORT = CodeTypeImpl.createPrimitive("short", "Short", List.of());
  CodePrimitiveType INT = CodeTypeImpl.createPrimitive("int", "Integer", List.of());
  CodePrimitiveType LONG = CodeTypeImpl.createPrimitive("long", "Long", List.of());
  CodePrimitiveType FLOAT = CodeTypeImpl.createPrimitive("float", "Float", List.of());
  CodePrimitiveType DOUBLE = CodeTypeImpl.createPrimitive("double", "Double", List.of());
  CodePrimitiveType BOOL = CodeTypeImpl.createPrimitive("boolean", "Boolean", List.of());

  //
  // Access.
  //

  String name();

  CodeClassType boxedType();

  //
  // Modification.
  //

  @Override
  CodePrimitiveType withAnnotations(List<? extends ConvertToAnnotation> annotations);

  @Override
  default CodePrimitiveType withAnnotations(ConvertToAnnotation... annotations) {
    return withAnnotations(List.of(annotations));
  }

  @Override
  CodePrimitiveType withoutAnnotations();

  //
  // Util and interface impl.
  //

  @Override
  default String simpleName() {
    return name();
  }

  @Override
  default String fullyQualifiedName() {
    return name();
  }
}
