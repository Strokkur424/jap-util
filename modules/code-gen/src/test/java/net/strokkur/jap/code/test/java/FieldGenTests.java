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
package net.strokkur.jap.code.test.java;

import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

class FieldGenTests extends AbstractGenTest {

  @Test
  void testField() {
    // language=java
    final String expectedCode = """
      public static final @NonNull String SOME_FIELD = "Some value";
      """;

    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      JavaTypes.STRING,
      JSpecifyTypes.NON_NULL
    );

    final CodeVisitable ast = CodeField.builder(JavaTypes.STRING, "SOME_FIELD")
      .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC, Modifiers.FINAL)
      .addAnnotations(JSpecifyTypes.NON_NULL)
      .setInitializer(Expressions.string("Some value"))
      .toField();

    check(expectedImports, expectedCode, ast);
  }

  @Test
  void testSameInstance() {
    final CodeField field = CodeField.builder(JavaTypes.STRING, "field").toField();
    assertSame(field, field.toField());
  }
}
