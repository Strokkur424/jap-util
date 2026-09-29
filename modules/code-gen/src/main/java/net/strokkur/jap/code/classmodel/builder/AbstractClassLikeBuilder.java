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
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.convert.ConvertToField;
import net.strokkur.jap.code.convert.ConvertToMethod;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.convert.ConvertToClassType;
import net.strokkur.jap.code.type.generics.CodeGenericTypeDeclaration;
import net.strokkur.jap.code.util.Modifiers;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
abstract class AbstractClassLikeBuilder<R extends AbstractClassLikeBuilder<R>> {
  protected final CodeClassType type;
  protected final Set<Modifiers> modifiers = new HashSet<>();
  protected final List<CodeAnnotation> annotations = new ArrayList<>();
  protected final List<CodeField> fields = new ArrayList<>();
  protected final List<CodeMethod> methods = new ArrayList<>();

  protected @Nullable CodeDocumentation documentation;

  AbstractClassLikeBuilder(ConvertToClassType type) {
    this.type = type.toClassType();
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R setDocumentation(CodeDocumentation documentation) {
    this.documentation = documentation;
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addModifiers(Modifiers... modifiers) {
    this.modifiers.addAll(Set.of(modifiers));
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addAnnotations(ConvertToAnnotation... annotations) {
    this.annotations.addAll(Arrays.stream(annotations)
      .map(ConvertToAnnotation::toAnnotation)
      .toList()
    );
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addFields(ConvertToField... fields) {
    this.fields.addAll(Arrays.stream(fields)
      .map(ConvertToField::toField)
      .toList()
    );
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addMethods(ConvertToMethod... methods) {
    this.methods.addAll(Arrays.stream(methods)
      .map(ConvertToMethod::toMethod)
      .toList()
    );
    return (R) this;
  }

  abstract static class Typed<R extends Typed<R>> extends AbstractClassLikeBuilder<R> {
    protected final List<CodeGenericTypeDeclaration> genericTypes = new ArrayList<>();

    Typed(ConvertToClassType type) {
      super(type);
    }

    @Contract(value = "_ -> this", mutates = "this")
    public R addGenericTypes(CodeGenericTypeDeclaration... generics) {
      this.genericTypes.addAll(List.of(generics));
      return (R) this;
    }
  }
}
