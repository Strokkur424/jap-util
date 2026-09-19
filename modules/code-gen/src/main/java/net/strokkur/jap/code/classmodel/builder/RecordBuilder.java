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
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodePrimaryConstructor;
import net.strokkur.jap.code.classmodel.CodeRecord;
import net.strokkur.jap.code.classmodel.CodeRecordComponent;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToConstructor;
import net.strokkur.jap.code.convert.ConvertToField;
import net.strokkur.jap.code.convert.ConvertToMethod;
import net.strokkur.jap.code.convert.ConvertToPrimaryConstructor;
import net.strokkur.jap.code.convert.ConvertToType;
import net.strokkur.jap.code.type.CodeClassType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class RecordBuilder extends AbstractClassLikeBuilder<RecordBuilder> {
  private final List<CodeClassType> implementsInterfaces = new ArrayList<>();
  private final List<CodeRecordComponent> components = new ArrayList<>();

  private @Nullable CodePrimaryConstructor primaryConstructor = null;
  private final List<CodeConstructor> constructors = new ArrayList<>();
  private final List<CodeMethod> methods = new ArrayList<>();
  private final List<CodeField> fields = new ArrayList<>();

  public RecordBuilder(ConvertToClassType type) {
    super(type);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder addFields(ConvertToField... fields) {
    this.fields.addAll(Arrays.stream(fields)
      .map(ConvertToField::toField)
      .toList()
    );
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder addMethods(ConvertToMethod... methods) {
    this.methods.addAll(Arrays.stream(methods)
      .map(ConvertToMethod::toMethod)
      .toList()
    );
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder withPrimaryConstructor(ConvertToPrimaryConstructor ctor) {
    this.primaryConstructor = ctor.toPrimaryConstructor();
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder withPrimaryConstructor(Consumer<PrimaryConstructorBuilder> consumer) {
    final PrimaryConstructorBuilder builder = CodePrimaryConstructor.builder(this.type);
    consumer.accept(builder);
    this.primaryConstructor = builder.toPrimaryConstructor();
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder addConstructor(Consumer<ConstructorBuilder> consumer) {
    final ConstructorBuilder builder = CodeConstructor.builder(this.type);
    consumer.accept(builder);
    return addConstructor(builder);
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder addConstructor(ConvertToConstructor... constructors) {
    this.constructors.addAll(Arrays.stream(constructors)
      .map(ConvertToConstructor::toConstructor)
      .toList()
    );
    return this;
  }

  @Contract(value = "_,_,_ -> this", mutates = "this")
  public RecordBuilder addComponent(ConvertToType type, String name, CodeAnnotation... annotations) {
    this.components.add(CodeRecordComponent.of(type, name, annotations));
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder addComponents(CodeRecordComponent... components) {
    this.components.addAll(List.of(components));
    return this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public RecordBuilder implementsInterfaces(ConvertToClassType... implementsInterfaces) {
    this.implementsInterfaces.addAll(Arrays.stream(implementsInterfaces)
      .map(ConvertToClassType::toClassType)
      .toList());
    return this;
  }

  @Contract(pure = true)
  public CodeRecord toRecord() {
    return new CodeRecord(
      type,
      List.copyOf(genericTypes),
      Set.copyOf(modifiers),
      List.copyOf(annotations),
      List.copyOf(implementsInterfaces),
      List.copyOf(components),
      List.copyOf(fields),
      List.copyOf(methods),
      primaryConstructor,
      List.copyOf(constructors),
      documentation
    );
  }
}
