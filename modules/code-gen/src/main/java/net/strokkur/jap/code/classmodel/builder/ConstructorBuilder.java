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
import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToConstructor;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConstructorBuilder implements ConvertToConstructor {
  private final CodeClassType type;
  private @Nullable CodeDocumentation documentation = null;
  private @Nullable CodeBlock codeBlock = null;

  private final List<CodeGenericTypeDefinition> generics = new ArrayList<>();
  private final List<CodeAnnotation> annotations = new ArrayList<>();
  private final Set<Modifiers> modifiers = new HashSet<>();
  private final List<CodeClassType> throwsExceptions = new ArrayList<>();
  private final List<CodeParameterDefinition> parameters = new ArrayList<>();

  public ConstructorBuilder(CodeClassType type) {
    this.type = type;
  }

  public ConstructorBuilder setDocumentation(CodeDocumentation documentation) {
    this.documentation = documentation;
    return this;
  }

  public ConstructorBuilder setCodeBlock(ConvertToStatement... statements) {
    this.codeBlock = CodeBlock.of(statements);
    return this;
  }

  public ConstructorBuilder addAnnotations(CodeAnnotation... annotations) {
    this.annotations.addAll(List.of(annotations));
    return this;
  }

  public ConstructorBuilder addGenerics(CodeGenericTypeDefinition... generics) {
    this.generics.addAll(List.of(generics));
    return this;
  }

  public ConstructorBuilder addModifiers(Modifiers... modifiers) {
    this.modifiers.addAll(List.of(modifiers));
    return this;
  }

  public ConstructorBuilder addThrowsExceptions(ConvertToClassType... throwsExceptions) {
    this.throwsExceptions.addAll(Arrays.stream(throwsExceptions)
      .map(ConvertToClassType::toClassType)
      .toList()
    );
    return this;
  }

  public ConstructorBuilder addParameters(CodeParameterDefinition... parameters) {
    this.parameters.addAll(List.of(parameters));
    return this;
  }

  @Override
  public CodeConstructor toConstructor() {
    return new CodeConstructor(
      type,
      List.copyOf(generics),
      List.copyOf(annotations),
      Set.copyOf(modifiers),
      List.copyOf(throwsExceptions),
      documentation,
      List.copyOf(parameters),
      Objects.requireNonNull(codeBlock)
    );
  }
}
