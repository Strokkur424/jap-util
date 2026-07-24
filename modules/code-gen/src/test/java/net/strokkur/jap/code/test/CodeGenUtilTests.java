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
package net.strokkur.jap.code.test;

import net.strokkur.jap.code.CodeGenUtil;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGenUtilTests {

  private String createJavaFile() {
    final CodeClassType targetFile = CodeTypes.ofClass("net.strokkur.test.TargetClass");
    final CodeClass compiled = CodeClass.builder(targetFile)
      .setDocumentation(CodeDocumentation.text("A very simple example class to showcase the code gen feature."))
      .addModifiers(Modifiers.PUBLIC, Modifiers.FINAL)
      .addAnnotations(JSpecifyTypes.NULL_MARKED)
      .addFields(
        CodeField.builder(JavaTypes.RANDOM, "random")
          .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
          .setInitializer(JavaTypes.RANDOM.ctor()),
        CodeField.builder(JavaTypes.LIST.typed(JavaTypes.STRING), "welcomeMessages")
          .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
      )
      .addConstructor(ctor -> ctor
        .addModifiers(Modifiers.PUBLIC)
        .addParameter(JavaTypes.LIST.typed(JavaTypes.STRING), "welcomeMessages")
        .setCodeBlock(
          Expressions.thisExpr().chainField("welcomeMessages").assign(Expressions.variable("welcomeMessages"))
        )
      )
      .addMethods(CodeMethod.builder("greet")
        .addModifiers(Modifiers.PUBLIC)
        .setDocumentation(CodeDocumentation.text("Greet the console!"))
        .setCode(
          Statements.variableDeclarationFinal(
            JavaTypes.STRING,
            "message",
            Expressions.fieldAccess("welcomeMessages").chainMethod("get")
              .addParameters(Expressions.fieldAccess("random").chainMethod("nextInt")
                .addParameters(Expressions.fieldAccess("welcomeMessages").chainMethod("size"))
              )
          ),
          JavaTypes.SYSTEM.chainField("out").chainMethod("println").addParameters(Expressions.variable("message"))
        )
      )
      .build();

    return CodeGenUtil.createJavaFile(compiled);
  }

  @Test
  void testCreatedJavaFile() {
    // language=java
    final String expected = """
      package net.strokkur.test;
      
      import org.jspecify.annotations.NullMarked;
      
      import java.util.List;
      import java.util.Random;
      
      /**
       * A very simple example class to showcase the code gen feature.
       */
      @NullMarked
      public final class TargetClass {
        private final Random random = new Random();
        private final List<String> welcomeMessages;
      
        public TargetClass(List<String> welcomeMessages) {
          this.welcomeMessages = welcomeMessages;
        }
      
        /**
         * Greet the console!
         */
        public void greet() {
          final String message = welcomeMessages.get(random.nextInt(welcomeMessages.size()));
          System.out.println(message);
        }
      }
      """;

    assertEquals(expected, createJavaFile());
  }
}
