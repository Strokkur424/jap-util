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
import net.strokkur.jap.code.expression.source.MethodReferenceSource;
import net.strokkur.jap.code.type.convert.ConvertToType;
import net.strokkur.jap.code.type.generics.CodeEnclosable;
import net.strokkur.jap.code.type.impl.CodeArrayTypeImpl;
import org.jetbrains.annotations.Contract;

import java.util.List;

public sealed interface CodeArrayType
  extends CodeType, MethodReferenceSource, CodeEnclosable
  permits CodeArrayTypeImpl {

  //
  // Access.
  //

  @Contract(pure = true)
  CodeType inner();

  //
  // Modification
  //

  CodeArrayType withInner(ConvertToType inner);

  @Override
  CodeArrayType withAnnotations(List<? extends ConvertToAnnotation> annotations);

  @Override
  default CodeArrayType withAnnotations(ConvertToAnnotation... annotations) {
    return withAnnotations(List.of(annotations));
  }

  //
  // Util and interface impl.
  //

  @Override
  default String simpleName() {
    return inner().simpleName() + "[]";
  }

  @Override
  default String fullyQualifiedName() {
    return inner().simpleName() + "[]";
  }
}
