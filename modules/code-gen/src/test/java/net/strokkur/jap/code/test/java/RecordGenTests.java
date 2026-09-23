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
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeRecord;
import net.strokkur.jap.code.type.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generics.CodeGenericTypeDeclaration;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.junit.jupiter.api.Test;

import java.util.Set;

class RecordGenTests extends AbstractGenTest {

  @Test
  void testFullRecord() {
    //language=Java
    final String expectedCode = """
      /// Documentation
      @NullMarked
      public record Triple<L, M, R>(
        @Nullable String tripleName,
        L left,
        M middle,
        R right
      ) {
        public static final Triple<String, String, String> A = Triple.of("a");
      
        /// Constructs a Triple with all three same values.
        public static <S> Triple<S, S, S> of(S same) {
          return new Triple<>("same", same, same, same);
        }
      
        /// Constructs a new Triple
        public Triple {
          Objects.requireNonNull(left);
          Objects.requireNonNull(middle);
          Objects.requireNonNull(right);
        }
      
        @Override
        public String toString() {
          return "noString() :P";
        }
      }
      """;

    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      TestTypes.TRIPLE,
      JavaTypes.STRING,
      JavaTypes.OBJECTS,
      JavaTypes.OVERRIDE,
      JSpecifyTypes.NULL_MARKED,
      JSpecifyTypes.NULLABLE
    );

    final CodeVisitable ast = CodeRecord.builder(TestTypes.TRIPLE)
      .setDocumentation(CodeDocumentation.text("Documentation"))
      .addAnnotations(JSpecifyTypes.NULL_MARKED)
      .addModifiers(Modifiers.PUBLIC)
      .addGenericTypes(
        CodeGenericTypeDeclaration.of("L"),
        CodeGenericTypeDeclaration.of("M"),
        CodeGenericTypeDeclaration.of("R")
      )
      .addComponent(JavaTypes.STRING, "tripleName", JSpecifyTypes.NULLABLE)
      .addComponent(CodeTypes.generic("L"), "left")
      .addComponent(CodeTypes.generic("M"), "middle")
      .addComponent(CodeTypes.generic("R"), "right")

      .addFields(CodeField.builder(TestTypes.TRIPLE.typed(JavaTypes.STRING, JavaTypes.STRING, JavaTypes.STRING), "A")
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC, Modifiers.FINAL)
        .setInitializer(TestTypes.TRIPLE.chainMethod("of", Expressions.string("a")))
      )

      .withPrimaryConstructor(builder -> builder
        .setDocumentation(CodeDocumentation.text("Constructs a new Triple"))
        .addModifiers(Modifiers.PUBLIC)
        .setCodeBlock(
          JavaTypes.OBJECTS.chainMethod("requireNonNull", Expressions.variable("left")),
          JavaTypes.OBJECTS.chainMethod("requireNonNull", Expressions.variable("middle")),
          JavaTypes.OBJECTS.chainMethod("requireNonNull", Expressions.variable("right"))
        )
      )

      .addMethods(CodeMethod.builder("of")
        .setDocumentation(CodeDocumentation.text("Constructs a Triple with all three same values."))
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
        .addGenerics(CodeGenericTypeDeclaration.of("S"))
        .setReturnType(TestTypes.TRIPLE.typed(CodeTypes.generic("S"), CodeTypes.generic("S"), CodeTypes.generic("S")))
        .addParameter(CodeTypes.generic("S"), "same")
        .setCode(
          Statements.returnStmt(
            TestTypes.TRIPLE.typed().ctor(
              Expressions.string("same"),
              Expressions.variable("same"),
              Expressions.variable("same"),
              Expressions.variable("same")
            )
          )
        )
      )

      .addMethods(CodeMethod.builder("toString")
        .addAnnotations(JavaTypes.OVERRIDE)
        .addModifiers(Modifiers.PUBLIC)
        .setReturnType(JavaTypes.STRING)
        .setCode(Statements.returnStmt(
          Expressions.string("noString() :P")
        ))
      )

      .toRecord();

    check(expectedImports, expectedCode, ast);
  }

  @Test
  void testEmptyRecord() {
    checkCode("""
        record EmptyRecord() {
        }
        """,
      CodeRecord.builder("none.EmptyRecord").toRecord()
    );
  }
}
