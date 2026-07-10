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

import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.statement.CodeStatement;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.expression.Expressions.string;
import static net.strokkur.jap.code.expression.Expressions.variable;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatementGenTests extends AbstractGenTest {

  @Test
  void testReturn() {
    check(Set.of(), "return;\n", Statements.returnStmt());
    check(Set.of(), "return \"value\";\n", Statements.returnStmt(string("value")));
    check(Set.of(TestTypes.CUSTOM_TYPE), "return CustomType.get();\n", Statements.returnStmt(
      TestTypes.CUSTOM_TYPE.chainMethod("get")
    ));
  }

  @Test
  void testExpressionStmt() {
    check(
      Set.of(TestTypes.CUSTOM_TYPE),
      "CustomType.run();\n",
      TestTypes.CUSTOM_TYPE.chainMethod("run").toStatement()
    );
  }

  @Test
  void testThrow() {
    check(Set.of(), "throw \"value\";\n", Statements.throwStatement(string("value")));
    check(Set.of(TestTypes.CUSTOM_TYPE), "throw CustomType.get();\n", Statements.throwStatement(
      TestTypes.CUSTOM_TYPE.chainMethod("get")
    ));
  }

  @Test
  void testIfStatement() {
    final String expectedCode = """
      if (ctx.getSource() instanceof Player source) {
        source.sendPlainMessage("Hello!");
      }
      """;
    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      TestTypes.PLAYER
    );

    check(expectedImports, expectedCode, Statements.ifStmt(
      variable("ctx").chainMethod("getSource").instanceOf(TestTypes.PLAYER, "source"),
      variable("source").chainMethod("sendPlainMessage").addParameters(
        string("Hello!")
      )
    ));

    final String expectedInvertedCode = """
      if (!(ctx.getSource() instanceof Player)) {
        throw new IllegalStateException("Don't do that.");
      }
      """;
    final Set<? extends ConvertToClassType> expectedInvertedImports = Set.of(
      TestTypes.PLAYER,
      JavaTypes.ILLEGAL_STATE_EXCEPTION
    );
    check(expectedInvertedImports, expectedInvertedCode, Statements.ifStmt(
      variable("ctx").chainMethod("getSource").instanceOf(TestTypes.PLAYER).not(),
      JavaTypes.ILLEGAL_STATE_EXCEPTION.ctor(string("Don't do that.")).throwStmt()
    ));
  }

  @Test
  void testVariableDeclaration() {
    check(
      Set.of(JavaTypes.STRING, TestTypes.CUSTOM_TYPE),
      "String value = CustomType.get();\n",
      Statements.variableDeclaration(
        JavaTypes.STRING,
        "value",
        TestTypes.CUSTOM_TYPE.chainMethod("get")
      )
    );
    // Same thing but with final
    check(
      Set.of(JavaTypes.STRING, TestTypes.CUSTOM_TYPE),
      "final String value = CustomType.get();\n",
      Statements.variableDeclarationFinal(
        JavaTypes.STRING,
        "value",
        TestTypes.CUSTOM_TYPE.chainMethod("get")
      )
    );
    // Same thing again, but without assign
    check(
      Set.of(JavaTypes.STRING),
      "String value;\n",
      Statements.variableDeclaration(
        JavaTypes.STRING,
        "value",
        null
      )
    );
  }

  @Test
  void testBlank() {
    check(Set.of(), "\n", Statements.blank());
  }

  @Test
  void statementToStatementReturnsSelf() {
    final CodeStatement stmt = Statements.blank();
    assertSame(stmt, stmt.toStatement());
  }

  @Test
  void ensureStatementsCtorThrows() {
    assertThrows(IllegalAccessError.class, () -> constructReflectively(Statements.class));
  }
}
