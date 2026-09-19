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
package net.strokkur.jap.code.classmodel;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.builder.RecordBuilder;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public record CodeRecord(
  CodeClassType classType,
  List<CodeGenericTypeDefinition> genericTypes,
  Set<Modifiers> modifiers,
  List<CodeAnnotation> annotations,
  List<CodeClassType> implementsTypes,

  List<CodeRecordComponent> components,
  List<CodeField> fields,
  List<CodeMethod> methods,

  @Nullable CodePrimaryConstructor primaryConstructor,
  List<CodeConstructor> additionalConstructors,

  @Nullable CodeDocumentation documentation
) implements CodeClassLikeTyped {

  public static RecordBuilder builder(String fqn) {
    return builder(CodeTypes.of(fqn));
  }

  public static RecordBuilder builder(ConvertToClassType type) {
    return new RecordBuilder(type.toClassType());
  }

  @Override
  public <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitRecord(this);
  }
}
