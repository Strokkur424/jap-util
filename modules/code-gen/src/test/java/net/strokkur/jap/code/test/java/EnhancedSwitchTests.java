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
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.expression.Expressions.enhancedSwitch;
import static net.strokkur.jap.code.expression.Expressions.fieldAccess;
import static net.strokkur.jap.code.expression.Expressions.intExpr;
import static net.strokkur.jap.code.expression.Expressions.methodInvocation;
import static net.strokkur.jap.code.expression.Expressions.string;
import static net.strokkur.jap.code.expression.Expressions.variable;
import static net.strokkur.jap.code.statement.Statements.returnStmt;
import static net.strokkur.jap.code.statement.Statements.variableDeclarationFinal;
import static net.strokkur.jap.code.type.CodeTypes.generic;

class EnhancedSwitchTests extends AbstractGenTest {

  @Test
  void testSimple() {
    final String expected = """
      switch (val) {
        case "Boom!" -> IO.println("Explosion!!");
        case "Chill" -> {
          IO.println("Give me a second...");
          wait(50l);
        }
        case null -> throw new IllegalArgumentException("Why null?");
        default -> IO.println("Nice. Just nice.");
      }""";

    check(
      Set.of(CodeTypes.of(IllegalArgumentException.class)),
      expected,
      enhancedSwitch(variable("val"))
        .branchInline(string("Boom!"), variable("IO").chainMethod("println", string("Explosion!!")))
        .branchBlock(string("Chill"),
          variable("IO").chainMethod("println", string("Give me a second...")),
          methodInvocation("wait", Expressions.longExpr(50))
        )
        .branchInline(Expressions.nullExpr(), CodeTypes.of(IllegalArgumentException.class).ctor(string("Why null?")).throwExpr())
        .defaultInline(variable("IO").chainMethod("println", string("Nice. Just nice.")))
        .toEnhancedSwitch()
    );
  }

  @Test
  void testAssign() {
    final String expected = """
      final String out = switch (val) {
        case 10 -> "Ten";
        case 5 -> "Five";
        default -> "idfk";
      };
      """;

    check(
      Set.of(JavaTypes.STRING),
      expected,
      variableDeclarationFinal(
        JavaTypes.STRING,
        "out",
        enhancedSwitch(variable("val"))
          .branchInline(intExpr(10), string("Ten"))
          .branchInline(intExpr(5), string("Five"))
          .defaultInline(string("idfk"))
      )
    );
  }

  @Test
  void testMethodReturn() {
    final String expected = """
      public static <U> U construct(Class<U> clazz) {
        return switch (clazz) {
          case Class c when c == MyType.class -> (U) new MyType();
          case Class c when c == OtherType.class -> (U) new OtherType();
          default -> {
            LOGGER.error("Invalid type: " + clazz.getName());
            throw new IllegalArgumentException(clazz.getName() + " is not a valid class type");
          }
        };
      }
      """;

    final CodeClassType myType = CodeTypes.of("test.MyType");
    final CodeClassType otherType = CodeTypes.of("test.OtherType");

    check(
      Set.of(JavaTypes.CLASS, myType, otherType, CodeTypes.of(IllegalArgumentException.class)),
      expected,

      CodeMethod.builder("construct")
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
        .addGenerics(CodeGenericTypeDefinition.of("U"))
        .setReturnType(generic("U"))
        .addParameter(
          CodeTypes.of(Class.class).typed(generic("U")),
          "clazz"
        )
        .addCode(returnStmt(enhancedSwitch(variable("clazz"))
          .branchInline(JavaTypes.CLASS, "c", variable("c").eq(myType.chainField("class")), Expressions.cast(myType.ctor(), generic("U")))
          .branchInline(JavaTypes.CLASS, "c", variable("c").eq(otherType.chainField("class")), Expressions.cast(otherType.ctor(), generic("U")))
          .defaultBlock(
            fieldAccess("LOGGER").chainMethod("error", string("Invalid type: ").concat(variable("clazz").chainMethod("getName"))),
            CodeTypes.of(IllegalArgumentException.class).ctor(variable("clazz").chainMethod("getName").concat(" is not a valid class type")).throwStmt()
          )
        ))
        .toMethod()
    );
  }
}
