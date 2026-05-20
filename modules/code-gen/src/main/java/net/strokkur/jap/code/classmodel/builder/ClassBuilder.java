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
package net.strokkur.jap.code.classmodel.builder;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.convert.ConvertToConstructor;
import net.strokkur.jap.code.convert.ConvertToField;
import net.strokkur.jap.code.convert.ConvertToMethod;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassBuilder {
  private final CodeClassType type;
  private final List<CodeGenericTypeDefinition> genericTypes = new ArrayList<>();
  private final Set<Modifiers> modifiers = new HashSet<>();
  private final List<CodeAnnotation> annotations = new ArrayList<>();
  private final List<CodeField> fields = new ArrayList<>();
  private final List<CodeMethod> methods = new ArrayList<>();
  private final List<CodeConstructor> constructors = new ArrayList<>();

  private @Nullable CodeDocumentation documentation;

  public ClassBuilder(CodeClassType type) {
    this.type = type;
  }

  public ClassBuilder setDocumentation(CodeDocumentation documentation) {
    this.documentation = documentation;
    return this;
  }

  public ClassBuilder addGenericTypes(CodeGenericTypeDefinition... generics) {
    this.genericTypes.addAll(List.of(generics));
    return this;
  }

  public ClassBuilder addModifiers(Modifiers... modifiers) {
    this.modifiers.addAll(Set.of(modifiers));
    return this;
  }

  public ClassBuilder addAnnotations(CodeAnnotation... annotations) {
    this.annotations.addAll(List.of(annotations));
    return this;
  }

  public ClassBuilder addFields(ConvertToField... fields) {
    this.fields.addAll(Arrays.stream(fields)
      .map(ConvertToField::toField)
      .toList()
    );
    return this;
  }

  public ClassBuilder addMethods(ConvertToMethod... methods) {
    this.methods.addAll(Arrays.stream(methods)
      .map(ConvertToMethod::toMethod)
      .toList()
    );
    return this;
  }

  public ClassBuilder addConstructor(ConvertToConstructor... constructors) {
    this.constructors.addAll(Arrays.stream(constructors)
      .map(ConvertToConstructor::toConstructor)
      .toList()
    );
    return this;
  }

  public CodeClass build() {
    return new CodeClass(
      type,
      List.copyOf(genericTypes),
      Set.copyOf(modifiers),
      List.copyOf(annotations),
      List.copyOf(fields),
      List.copyOf(methods),
      List.copyOf(constructors),
      documentation
    );
  }
}
