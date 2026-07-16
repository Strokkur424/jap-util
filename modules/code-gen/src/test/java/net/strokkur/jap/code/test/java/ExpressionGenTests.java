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

import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.expression.FieldAccess;
import net.strokkur.jap.code.expression.simple.CodeBooleanExpression;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.StyleConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static net.strokkur.jap.code.expression.Expressions.string;
import static net.strokkur.jap.code.expression.Expressions.variable;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionGenTests extends AbstractGenTest {

  @Test
  void methodInvocation() {
    final String invocationCode = """
      String
         .getValue()
         .stream().toLol()
         .toList()""";
    check(Set.of(JavaTypes.STRING), invocationCode,
      JavaTypes.STRING
        .chainMethod("getValue").setStyle(StyleConfig.NEWLINE)
        .chainMethod("stream").setStyle(StyleConfig.NEWLINE)
        .chainMethod("toLol")
        .chainMethod("toList").setStyle(StyleConfig.NEWLINE)
    );

    final String nestedInvocationCode = """
      Commands.literal("hey")
         .then(Commands.literal("you")
            .then(Commands.literal("there!")
               .thisWorks()
            )
         )
         .build()""";
    check(Set.of(TestTypes.COMMANDS), nestedInvocationCode,
      TestTypes.COMMANDS
        .chainMethod("literal", Expressions.string("hey"))
        .chainMethod("then", StyleConfig.NEWLINE_BOTH,
          TestTypes.COMMANDS.chainMethod("literal", Expressions.string("you"))
            .chainMethod("then", StyleConfig.NEWLINE_BOTH,
              TestTypes.COMMANDS.chainMethod("literal", Expressions.string("there!"))
                .chainMethod("thisWorks", StyleConfig.NEWLINE)
            )
        )
        .chainMethod("build", StyleConfig.NEWLINE)
    );
  }

  @Test
  void testCtorInvocation() {
    final String expectedCode = """
      variable
         .new Player("Name")""";
    check(Set.of(TestTypes.PLAYER), expectedCode, Expressions.ctorInvocation(TestTypes.PLAYER)
      .setSource(variable("variable"))
      .addParameters(Expressions.string("Name"))
      .setStyle(StyleConfig.NEWLINE)
      .toExpression() // only difference
    );
    check(Set.of(TestTypes.PLAYER), expectedCode, Expressions.ctorInvocation(TestTypes.PLAYER)
      .setSource(variable("variable"))
      .addParameters(Expressions.string("Name"))
      .setStyle(StyleConfig.NEWLINE)
    );
  }

  @Test
  void testMethodReference() {
    check(Set.of(JavaTypes.STRING), "String::runCode", JavaTypes.STRING.methodReference("runCode"));
    check(Set.of(), "var::runCode", Expressions.variable("var").methodReference("runCode"));
  }

  @Test
  void testFieldAccess() {
    check(Set.of(), "value", Expressions.fieldAccess("value"));
    check(Set.of(), "inst.value", Expressions.variable("inst").chainField("value"));
    check(Set.of(TestTypes.MY_CLASS), "MyClass.value", TestTypes.MY_CLASS.chainField("value"));
    check(Set.of(TestTypes.MY_CLASS), "MyClass.fetch().value", TestTypes.MY_CLASS
      .chainMethod("fetch")
      .chainField("value"));
    check(Set.of(TestTypes.CUSTOM_TYPE), "CustomType.another.value", TestTypes.CUSTOM_TYPE
      .chainField("another")
      .chainField("value")
    );
  }

  @Test
  void testLambda() {
    final String simpleLambdaParam = """
      builder.requires(source -> source.hasPermission("testcommand.use"));
      """;
    check(Set.of(), simpleLambdaParam, variable("builder")
      .chainMethod("requires")
      .addParameters(Expressions.lambdaInline("source", variable("source").chainMethod("hasPermission")
        .addParameters(string("testcommand.use"))
      ))
      .toStatement()
    );

    final String simpleMultilineLambdaParam = """
      builder.executes(ctx -> {
        instance.run(ctx.getSource());
        return;
      });
      """;
    check(Set.of(), simpleMultilineLambdaParam, variable("builder")
      .chainMethod("executes").addParameters(
        Expressions.lambda("ctx",
          variable("instance").chainMethod("run")
            .addParameters(variable("ctx").chainMethod("getSource")),
          Statements.returnStmt()
        ))
      .toStatement()
    );

    final String singleStatementMultilineLambdaParam = """
      launchTask(() -> {
        run("Second");
      }, "First");
      """;
    check(
      Set.of(),
      singleStatementMultilineLambdaParam,
      Expressions.methodInvocation("launchTask")
        .addParameters(
          Expressions.lambda(Expressions.methodInvocation("run")
            .addParameters(string("Second"))
          ),
          string("First")
        ).toStatement()
    );

    final String testMultiParam = """
      sortBy((a, b) -> Integer.compare(a, b));
      """;
    check(Set.of(JavaTypes.INTEGER), testMultiParam, Expressions.methodInvocation("sortBy")
      .addParameters(
        Expressions.lambdaInline(List.of("a", "b"),
          JavaTypes.INTEGER.chainMethod("compare")
            .addParameters(variable("a"), variable("b"))
        )
      ).toStatement()
    );

    check(Set.of(TestTypes.CUSTOM_TYPE), "() -> CustomType.get()",
      Expressions.lambdaInline(TestTypes.CUSTOM_TYPE.chainMethod("get"))
    );
  }

  @Test
  void testInstanceOf() {
    check(Set.of(TestTypes.PLAYER), "ctx instanceof Player", variable("ctx").instanceOf(TestTypes.PLAYER));
    check(Set.of(TestTypes.PLAYER), "!(ctx instanceof Player)", variable("ctx").instanceOf(TestTypes.PLAYER).not());
    check(Set.of(TestTypes.PLAYER), "ctx instanceof Player src", variable("ctx").instanceOf(TestTypes.PLAYER, "src"));
    check(Set.of(TestTypes.PLAYER), "!(ctx instanceof Player src)", variable("ctx").instanceOf(TestTypes.PLAYER, "src").not());
  }

  @Test
  void testAssign() {
    check(Set.of(), "variable = value", variable("variable").assign(variable("value")));
  }

  @Test
  void testSimpleExpressions() {
    check(Set.of(), "24.0d", Expressions.doubleExpr(24));
    check(Set.of(), "24.0f", Expressions.floatExpr(24));
    check(Set.of(), "24l", Expressions.longExpr(24));
    check(Set.of(), "24", Expressions.intExpr(24));

    check(Set.of(), "null", Expressions.nullExpr());
    check(Set.of(), "\"a string\"", Expressions.string("a string"));
    check(Set.of(), "variable", Expressions.variable("variable"));
    check(Set.of(), "this", Expressions.thisExpr());

    check(Set.of(), "true", Expressions.bool(true));
    check(Set.of(), "false", Expressions.bool(false));
    check(Set.of(), "true", Expressions.bool(false).not());
    check(Set.of(), "false", Expressions.bool(true).not());

    assertInstanceOf(CodeBooleanExpression.class, Expressions.bool(true).not());
    assertInstanceOf(CodeBooleanExpression.class, Expressions.bool(false).not());
  }

  @Test
  void ensureSameInstances() {
    final FieldAccess access = JavaTypes.STRING.chainField("hey");
    assertSame(access, access.toFieldAccess());

    final CodeBooleanExpression booleanExpr = Expressions.bool(true);
    assertSame(booleanExpr, booleanExpr.toExpression());
  }

  @Test
  void ensureExpressionsCtorThrows() {
    assertThrows(IllegalAccessError.class, () -> constructReflectively(Expressions.class));
  }
}
