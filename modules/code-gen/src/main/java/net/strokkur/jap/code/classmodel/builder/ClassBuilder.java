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

import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.type.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToConstructor;
import net.strokkur.jap.code.type.CodeClassType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ClassBuilder extends AbstractClassLikeBuilder.Typed<ClassBuilder> {
  private final List<CodeConstructor> constructors = new ArrayList<>();
  private @Nullable CodeClassType extendsClass = null;
  private final List<CodeClassType> implementsInterfaces = new ArrayList<>();

  public ClassBuilder(ConvertToClassType type) {
    super(type);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public ClassBuilder addConstructor(Consumer<ConstructorBuilder> consumer) {
    final ConstructorBuilder builder = CodeConstructor.builder(this.type);
    consumer.accept(builder);
    return addConstructor(builder);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public ClassBuilder addConstructor(ConvertToConstructor... constructors) {
    this.constructors.addAll(Arrays.stream(constructors)
      .map(ConvertToConstructor::toConstructor)
      .toList()
    );
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public ClassBuilder extendsClass(@Nullable ConvertToClassType extendsClass) {
    this.extendsClass = extendsClass != null ? extendsClass.toClassType() : null;
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public ClassBuilder implementsInterfaces(ConvertToClassType... implementsInterfaces) {
    this.implementsInterfaces.addAll(Arrays.stream(implementsInterfaces)
      .map(ConvertToClassType::toClassType)
      .toList());
    return this;
  }

  public CodeClass toClass() {
    return new CodeClass(
      type,
      List.copyOf(genericTypes),
      Set.copyOf(modifiers),
      List.copyOf(annotations),
      extendsClass,
      List.copyOf(implementsInterfaces),
      List.copyOf(fields),
      List.copyOf(methods),
      List.copyOf(constructors),
      documentation
    );
  }

  @Contract(pure = true)
  @Deprecated(forRemoval = true)
  public CodeClass build() {
    return toClass();
  }
}
