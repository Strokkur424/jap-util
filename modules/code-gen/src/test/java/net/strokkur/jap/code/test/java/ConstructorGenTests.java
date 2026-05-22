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

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.expression.Expressions.variable;
import static net.strokkur.jap.code.type.CodeTypes.generic;
import static org.junit.jupiter.api.Assertions.assertSame;

class ConstructorGenTests extends AbstractGenTest {

  @Test
  void testConstructor() {
    // language=java
    final String expectedCode = """
      class MyClass {
      
        /// Creates an instance.
        @Nullable
        public <T> MyClass(@NonNull List<T> genericList, T firstEntry) throws RuntimeException {
          genericList.addFirst(firstEntry);
        }
      }
      """;

    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      JSpecifyTypes.NULLABLE,
      TestTypes.MY_CLASS,
      JSpecifyTypes.NON_NULL,
      JavaTypes.LIST,
      JavaTypes.RUNTIME_EXCEPTION
    );

    final CodeVisitable ast = CodeClass.builder(TestTypes.MY_CLASS)
      .addConstructor(ctor -> ctor
        .setDocumentation(CodeDocumentation.text("Creates an instance."))
        .addAnnotations(CodeAnnotation.of(JSpecifyTypes.NULLABLE))
        .addModifiers(Modifiers.PUBLIC)
        .addGenerics(CodeGenericTypeDefinition.of("T"))
        .addParameter(
          JavaTypes.LIST.typed(generic("T")),
          "genericList",
          CodeAnnotation.of(JSpecifyTypes.NON_NULL)
        )
        .addParameter(generic("T"), "firstEntry")
        .addThrowsExceptions(JavaTypes.RUNTIME_EXCEPTION)
        .setCodeBlock(
          variable("genericList").chainMethod("addFirst").addParameters(variable("firstEntry"))
        )
      )
      .build();

    check(expectedImports, expectedCode, ast);
  }

  @Test
  void testCtorReturnsItself() {
    final CodeConstructor ctor = CodeConstructor.builder(TestTypes.MY_CLASS).toConstructor();
    assertSame(ctor, ctor.toConstructor());
  }
}
