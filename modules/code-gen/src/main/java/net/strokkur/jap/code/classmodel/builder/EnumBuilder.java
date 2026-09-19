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
import net.strokkur.jap.code.classmodel.CodeEnum;
import net.strokkur.jap.code.classmodel.CodeEnumValue;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToConstructor;
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EnumBuilder extends AbstractClassLikeBuilder<EnumBuilder> {
  private final List<CodeClassType> implementsInterfaces = new ArrayList<>();
  private final List<CodeEnumValue> enumValues = new ArrayList<>();
  private final List<CodeConstructor> constructors = new ArrayList<>();

  public EnumBuilder(ConvertToClassType type) {
    super(type);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public EnumBuilder implementsInterfaces(ConvertToClassType... implementsInterfaces) {
    this.implementsInterfaces.addAll(Arrays.stream(implementsInterfaces)
      .map(ConvertToClassType::toClassType)
      .toList());
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public EnumBuilder addConstructor(Consumer<ConstructorBuilder> consumer) {
    final ConstructorBuilder builder = CodeConstructor.builder(this.type);
    consumer.accept(builder);
    return addConstructor(builder);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public EnumBuilder addConstructor(ConvertToConstructor... constructors) {
    this.constructors.addAll(Arrays.stream(constructors)
      .map(ConvertToConstructor::toConstructor)
      .toList()
    );
    return this;
  }

  @Contract(value = "_,_ -> this", mutates = "this")
  public EnumBuilder addValue(String name, ConvertToExpression... parameters) {
    this.enumValues.add(CodeEnumValue.of(name, parameters));
    return this;
  }

  @Contract(value = "_,_,_ -> this", mutates = "this")
  public EnumBuilder addValue(String name, CodeDocumentation documentation, ConvertToExpression... parameters) {
    this.enumValues.add(CodeEnumValue.of(name, documentation, parameters));
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public EnumBuilder addValues(CodeEnumValue... values) {
    this.enumValues.addAll(List.of(values));
    return this;
  }

  public CodeEnum toEnum() {
    return new CodeEnum(
      type,
      Set.copyOf(modifiers),
      List.copyOf(annotations),
      List.copyOf(implementsInterfaces),
      List.copyOf(enumValues),
      List.copyOf(fields),
      List.copyOf(constructors),
      List.copyOf(methods),
      documentation
    );
  }
}
