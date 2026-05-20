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
import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToField;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.util.Modifiers;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldBuilder implements ConvertToField {
  private final CodeType type;
  private final String name;
  private @Nullable CodeExpression initializer;

  private final Set<Modifiers> modifiers = new HashSet<>();
  private final List<CodeAnnotation> annotations = new ArrayList<>();

  public FieldBuilder(CodeType type, String name) {
    this.type = type;
    this.name = name;
  }

  public FieldBuilder setInitializer(ConvertToExpression initializer) {
    this.initializer = initializer.toExpression();
    return this;
  }

  public FieldBuilder addModifiers(Modifiers... modifiers) {
    this.modifiers.addAll(List.of(modifiers));
    return this;
  }

  public FieldBuilder addAnnotations(CodeAnnotation... annotations) {
    this.annotations.addAll(List.of(annotations));
    return this;
  }

  @Override
  public CodeField toField() {
    return new CodeField(
      type,
      name,
      Set.copyOf(modifiers),
      List.copyOf(annotations),
      initializer
    );
  }
}
