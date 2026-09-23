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
package net.strokkur.jap.code.type.convert;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToFieldMethodSource;
import net.strokkur.jap.code.convert.ConvertToMethodReferenceSource;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.expression.builder.ConstructorInvocationBuilder;
import net.strokkur.jap.code.expression.source.MethodReferenceSource;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import net.strokkur.jap.code.type.generic.GenericEnclosure;

import java.util.List;

public interface ConvertToClassType extends ConvertToType, ConvertToGenericType, ConvertToFieldMethodSource, ConvertToMethodReferenceSource, ConvertToAnnotation {

  /// Converts this class into a [CodeClassType].
  CodeClassType toClassType();

  //
  // Modification
  //

  /// Returns this [CodeClassType] with any generic types cleared.
  @Override
  default CodeClassType withoutGenerics() {
    return toClassType().withoutGenerics();
  }

  /// Returns this [CodeClassType] typed with the provided generics. If no arguments are provided,
  /// the type will act as if it had the **diamond operator**: `ArrayList<>`.
  default CodeClassType typed(List<? extends ConvertToGenericType> types) {
    return toClassType().typed(types);
  }

  /// Returns this [CodeClassType] typed with the provided generics. If no arguments are provided,
  /// the type will act as if it had the **diamond operator**: `ArrayList<>`.
  default CodeClassType typed(ConvertToGenericType... types) {
    return toClassType().typed(types);
  }

  @Override
  default CodeClassType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return toClassType().withAnnotations(annotations);
  }

  @Override
  default CodeClassType withAnnotations(ConvertToAnnotation... annotations) {
    return toClassType().withAnnotations(annotations);
  }

  @Override
  default CodeClassType withoutAnnotations() {
    return withAnnotations();
  }

  //
  // Util
  //

  default ConstructorInvocationBuilder ctor(ConvertToExpression... parameters) {
    return Expressions.ctorInvocation(this)
      .addParameters(parameters);
  }

  //
  // Interface impl
  //

  @Override
  default MethodReferenceSource toMethodReferenceSource() {
    return toClassType();
  }

  @Override
  default CodeClassType toType() {
    return toClassType();
  }

  @Override
  default CodeGenericType toGenericType() {
    return new CodeGenericType(null, GenericEnclosure.withType(this), List.of());
  }

  @Override
  default CodeAnnotation toAnnotation() {
    return CodeAnnotation.of(this);
  }
}
