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

import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.convert.ConvertToConstructor;
import net.strokkur.jap.code.type.convert.ConvertToType;
import net.strokkur.jap.code.type.CodeClassType;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConstructorBuilder extends AbstractConstructorLikeBuilder<ConstructorBuilder> implements ConvertToConstructor {
  private final List<CodeParameterDefinition> parameters = new ArrayList<>();

  public ConstructorBuilder(CodeClassType type) {
    super(type);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public ConstructorBuilder addParameters(CodeParameterDefinition... parameters) {
    this.parameters.addAll(List.of(parameters));
    return this;
  }

  @Contract(value = "_,_,_ -> this", mutates = "this")
  public ConstructorBuilder addParameter(ConvertToType type, String name, ConvertToAnnotation... annotations) {
    return addParameters(CodeParameterDefinition.of(type, name, annotations));
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  public ConstructorBuilder addParameter(ConvertToType type, String name) {
    return addParameters(CodeParameterDefinition.of(type, name));
  }

  @Override
  @Contract(pure = true)
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
