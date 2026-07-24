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

import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.expression.Expressions.variable;
import static net.strokkur.jap.code.type.CodeTypes.generic;
import static org.junit.jupiter.api.Assertions.assertSame;

class MethodGenTests extends AbstractGenTest {

  @Test
  void testMethodClass() {
    // language=java
    final String expectedCode = """
      /// Adds a value to a list, returning the list.
      @NonNull
      private static <T> List<? extends T> add(List<? extends T> list, T value) throws RuntimeException, NullPointerException {
        list.add(value);
        return list;
      }
      """;

    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      JSpecifyTypes.NON_NULL,
      JavaTypes.LIST,
      JavaTypes.RUNTIME_EXCEPTION,
      JavaTypes.NULL_POINTER_EXCEPTION
    );

    final CodeType listType = JavaTypes.LIST.typed(CodeTypes.genericWildcardEnclosure(GenericEnclosure.withExtends(generic("T"))));
    final CodeVisitable ast = CodeMethod.builder("add")
      .setDocumentation(CodeDocumentation.text("Adds a value to a list, returning the list."))
      .addAnnotations(JSpecifyTypes.NON_NULL)
      .addModifiers(Modifiers.PRIVATE, Modifiers.STATIC)
      .addGenerics(CodeGenericTypeDefinition.of("T"))
      .setReturnType(listType)
      .addThrowsExceptions(JavaTypes.RUNTIME_EXCEPTION, JavaTypes.NULL_POINTER_EXCEPTION)
      .addParameter(listType, "list")
      .addParameters(CodeParameterDefinition.of(generic("T"), "value"))
      .setCode(
        variable("list").chainMethod("add").addParameters(variable("value")),
        Statements.returnStmt(variable("list"))
      )
      .toMethod();

    check(expectedImports, expectedCode, ast);
  }

  @Test
  void ensureMethodInstancesAreTheSame() {
    final CodeMethod method = CodeMethod.builder("some").toMethod();
    assertSame(method, method.toMethod());
  }
}
