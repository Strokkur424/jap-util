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

import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeInterface;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToField;
import net.strokkur.jap.code.convert.ConvertToMethod;
import net.strokkur.jap.code.type.CodeClassType;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class InterfaceBuilder extends AbstractClassLikeBuilder<InterfaceBuilder> {
  private final List<CodeField> fields = new ArrayList<>();
  private final List<CodeMethod> methods = new ArrayList<>();
  private final List<CodeClassType> extendsInterfaces = new ArrayList<>();

  public InterfaceBuilder(ConvertToClassType type) {
    super(type);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public InterfaceBuilder addFields(ConvertToField... fields) {
    this.fields.addAll(Arrays.stream(fields)
      .map(ConvertToField::toField)
      .toList()
    );
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public InterfaceBuilder addMethods(ConvertToMethod... methods) {
    this.methods.addAll(Arrays.stream(methods)
      .map(ConvertToMethod::toMethod)
      .toList()
    );
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public InterfaceBuilder extendsInterfaces(ConvertToClassType... implementsInterfaces) {
    this.extendsInterfaces.addAll(Arrays.stream(implementsInterfaces)
      .map(ConvertToClassType::toClassType)
      .toList());
    return this;
  }

  public CodeInterface toInterface() {
    return new CodeInterface(
      type,
      List.copyOf(genericTypes),
      Set.copyOf(modifiers),
      List.copyOf(annotations),
      List.copyOf(extendsInterfaces),
      List.copyOf(fields),
      List.copyOf(methods),
      documentation
    );
  }
}
