/*
 * This file is part of code-gen, licensed under the MIT License.
 *
 * Copyright (c) 2025 Strokkur24
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
package net.strokkur.jap.code.gen;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.documentation.MarkdownJavadocRenderer;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.util.StyleConfig;
import net.strokkur.jap.code.util.TestTypes;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.source.JavaSourcePrintingVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.strokkur.jap.code.expression.Expressions.string;
import static net.strokkur.jap.code.expression.Expressions.variable;
import static net.strokkur.jap.code.type.CodeTypes.generic;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaCodeGenTests {
  private static final CodeClass EXAMPLE_CLASS = CodeClass.builder("com.example.ExampleClass").build();

  private void check(String expected, CodeVisitable visitable) {
    final JavaSourcePrintingVisitor visitor = new JavaSourcePrintingVisitor(MarkdownJavadocRenderer::new, "  ", "   ");
    final String actual = visitable.accept(visitor).toString();
    assertEquals(expected, actual);
  }

  @Test
  void testClass() {
    // language=java
    final String expected = """
      class ExampleClass {
      }
      """;
    check(expected, EXAMPLE_CLASS);

    // language=java
    final String expectedWithFields = """
      class ExampleClass {
        String oneField;
        int anotherField;
      }
      """;
    check(expectedWithFields, CodeClass.builder(EXAMPLE_CLASS)
      .addFields(CodeField.builder(JavaTypes.STRING, "oneField"))
      .addFields(CodeField.builder(CodePrimitiveType.INT, "anotherField"))
      .build()
    );

    // language=java
    final String expectedWithMethods = """
      class ExampleClass {
      
        String oneMethod() {
        }
      
        int anotherMethod() {
        }
      }
      """;
    check(expectedWithMethods, CodeClass.builder(EXAMPLE_CLASS)
      .addMethods(
        CodeMethod.builder("oneMethod")
          .setReturnType(JavaTypes.STRING),
        CodeMethod.builder("anotherMethod")
          .setReturnType(CodePrimitiveType.INT)
      )
      .build()
    );

    // language=java
    final String expectedCombined = """
      class ExampleClass {
        String oneField;
        int anotherField;
      
        static String oneStaticMethod() {
        }
      
        static int anotherStaticMethod() {
        }
      
        ExampleClass() {
        }
      
        String oneInstanceMethod() {
        }
      
        int anotherInstanceMethod() {
        }
      }
      """;
    check(expectedCombined, CodeClass.builder(EXAMPLE_CLASS)
      .addFields(CodeField.builder(JavaTypes.STRING, "oneField"))
      .addFields(CodeField.builder(CodePrimitiveType.INT, "anotherField"))

      .addMethods(
        CodeMethod.builder("oneInstanceMethod")
          .setReturnType(JavaTypes.STRING),
        CodeMethod.builder("anotherInstanceMethod")
          .setReturnType(CodePrimitiveType.INT),

        CodeMethod.builder("oneStaticMethod")
          .addModifiers(Modifiers.STATIC)
          .setReturnType(JavaTypes.STRING),
        CodeMethod.builder("anotherStaticMethod")
          .addModifiers(Modifiers.STATIC)
          .setReturnType(CodePrimitiveType.INT)
      )
      .addConstructor(CodeConstructor.builder(EXAMPLE_CLASS))
      .build()
    );
  }

  @Test
  void testMethod() {
    // language=java
    final String expected = """
      void method() {
      }
      """;
    check(expected, CodeMethod.builder("method").toMethod());

    // language=java
    final String expectedWithModifiers = """
      public static void method() {
      }
      """;
    check(expectedWithModifiers, CodeMethod.builder("method")
      .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
      .toMethod()
    );

    // language=java
    final String expectedWithStatements = """
      void method() {
        otherMethod(STATIC_VALUE, "Now");
      }
      """;
    check(expectedWithStatements, CodeMethod.builder("method")
      .setCodeBlock(Expressions.methodInvocation("otherMethod")
        .addParameters(Expressions.fieldAccess("STATIC_VALUE"))
        .addParameters(string("Now"))
        .toStatement()
      )
      .toMethod()
    );

    // language=java
    final String expectedWithThrows = """
      void method() throws NullPointerException, SQLException {
        throw new SQLException("No database present :(");
      }
      """;
    check(expectedWithThrows, CodeMethod.builder("method")
      .addThrowsExceptions(
        JavaTypes.NULL_POINTER_EXCEPTION,
        CodeTypes.ofClass("java.sql.SQLException")
      )
      .setCodeBlock(
        Statements.throwStatement(Expressions.ctorInvocation(CodeTypes.ofClass("java.sql.SQLException"))
          .addParameters(string("No database present :("))
        )
      )
      .toMethod()
    );
  }

  @Test
  void testField() {
    // language=java
    final String expectedNoInit = """
      String someField;
      """;
    check(expectedNoInit, CodeField.builder(JavaTypes.STRING, "someField").toField());

    // language=java
    final String expectedWithInit = """
      String someField = "some value";
      """;
    check(expectedWithInit, CodeField.builder(JavaTypes.STRING, "someField")
      .setInitializer(string("some value"))
      .toField()
    );

    // language=java
    final String expectedWithModifiers = """
      public static final String SOME_FIELD;
      """;
    check(expectedWithModifiers, CodeField.builder(JavaTypes.STRING, "SOME_FIELD")
      .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC, Modifiers.FINAL)
      .toField()
    );
  }

  @Test
  void testStatement() {
    final String expectedReturnStmtEmpty = """
      return;
      """;
    check(expectedReturnStmtEmpty, Statements.returnStmt(null));

    final String expectedReturnStmtWithExpr = """
      return value;
      """;
    check(expectedReturnStmtWithExpr, Statements.returnStmt(
      variable("value")
    ));

    final String expectedVariableDeclaration = """
      String value = "burger";
      """;
    check(expectedVariableDeclaration, Statements.variableDeclaration(
      JavaTypes.STRING,
      "value",
      string("burger")
    ));

    final String expectedVariableDeclarationNoInit = """
      String value;
      """;
    check(expectedVariableDeclarationNoInit, Statements.variableDeclaration(
      JavaTypes.STRING,
      "value",
      null
    ));
  }

  @Test
  void methodInvocationFormat() {
    final String multiline = """
      String
         .getValue()
         .stream().toLol()
         .toList();
      """;
    check(multiline, Statements.expressionStatement(
        JavaTypes.STRING
          .chainMethod("getValue").setStyle(StyleConfig.NEWLINE)
          .chainMethod("stream").setStyle(StyleConfig.NEWLINE)
          .chainMethod("toLol")
          .chainMethod("toList").setStyle(StyleConfig.NEWLINE)
      )
    );
  }

  @Test
  void testLambda() {
    final String simpleLambdaParam = """
      builder.requires(source -> source.hasPermission("testcommand.use"));
      """;
    check(simpleLambdaParam, variable("builder")
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
    check(simpleMultilineLambdaParam, variable("builder")
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
    check(testMultiParam, Expressions.methodInvocation("sortBy")
      .addParameters(
        Expressions.lambdaInline(List.of("a", "b"),
          JavaTypes.INTEGER.chainMethod("compare")
            .addParameters(variable("a"), variable("b"))
        )
      ).toStatement()
    );
  }

  @Test
  void testIfStatement() {
    final String expected = """
      if (ctx.getSource() instanceof Player source) {
        source.sendPlainMessage("Hello!");
      }
      """;
    check(expected, Statements.ifStmt(
        variable("ctx").chainMethod("getSource").instanceOf(TestTypes.PLAYER, "source"),
        variable("source").chainMethod("sendPlainMessage").addParameters(
          string("Hello!")
        )
      )
    );

    final String expectedInverted = """
      if (!(ctx.getSource() instanceof Player)) {
        throw new IllegalStateException("Don't do that.");
      }
      """;
    check(expectedInverted, Statements.ifStmt(
      variable("ctx").chainMethod("getSource").instanceOf(TestTypes.PLAYER).invert(),
      JavaTypes.ILLEGAL_STATE_EXCEPTION.ctor(string("Don't do that.")).throwStmt()
    ));

    final String expectedWithNestedCtor = """
      if (!(ctx.getSource() instanceof Player)) {
        throw new SimpleCommandExceptionType(
           new LiteralMessage("This command requires a player sender!")
        ).create();
      }
      """;
    check(expectedWithNestedCtor, Statements.ifStmt(
      variable("ctx").chainMethod("getSource").instanceOf(TestTypes.PLAYER).invert(),
      TestTypes.SIMPLE_COMMAND_EXCEPTION_TYPE
        .ctor(TestTypes.LITERAL_MESSAGE.ctor(string("This command requires a player sender!")))
        .setStyle(StyleConfig.NEWLINE_MULTILINE)
        .chainMethod("create")
        .throwStmt()
    ));
  }

  @Test
  void testFieldAccess() {
    check("value", Expressions.fieldAccess("value"));
    check("inst.value", Expressions.variable("inst").chainField("value"));
    check("ClassType.value", CodeTypes.ofClass("some.ClassType").chainField("value"));
    check("ClassType.fetch().value", CodeTypes.ofClass("some.ClassType")
      .chainMethod("fetch")
      .chainField("value"));
    check("Yet.another.value", CodeTypes.ofClass("yet.Yet")
      .chainField("another")
      .chainField("value")
    );
  }

  @Test
  void testFullExecutesMethod() {
    final String expected = """
      builder.executes(ctx -> {
        if (!(ctx.getSource() instanceof Player source)) {
          throw new SimpleCommandExceptionType(
             new LiteralMessage("This command requires a player sender!")
          ).create();
        }
      
        instance.run(source);
        return Command.SINGLE_SUCCESS;
      });
      """;
    check(expected, variable("builder")
      .chainMethod("executes").addParameters(
        Expressions.lambda("ctx",

          // if-statement
          Statements.ifStmt(
            variable("ctx").chainMethod("getSource").instanceOf(TestTypes.PLAYER, "source").invert(),
            TestTypes.SIMPLE_COMMAND_EXCEPTION_TYPE
              .ctor(TestTypes.LITERAL_MESSAGE.ctor(string("This command requires a player sender!")))
              .setStyle(StyleConfig.NEWLINE_MULTILINE)
              .chainMethod("create")
              .throwStmt()
          ),
          Statements.blank(),

          variable("instance").chainMethod("run", variable("source")),
          Statements.returnStmt(TestTypes.COMMAND.chainField("SINGLE_SUCCESS"))
        ))
      .toStatement()
    );
  }

  @Test
  void testSimpleExpressions() {
    check("24.0d", Expressions.doubleExpr(24));
    check("24.0f", Expressions.floatExpr(24));
    check("24l", Expressions.longExpr(24));
    check("24", Expressions.intExpr(24));

    check("true", Expressions.bool(true));
    check("false", Expressions.bool(false));
  }

  @Test
  void testConstructor() {
    // language=java
    final String expected = """
      @NullMarked
      public class MyClass {
      
        /// Creates an instance.
        @Nullable
        public <T> MyClass(@NonNull List<T> genericList, T firstEntry) throws RuntimeException {
          genericList.addFirst(firstEntry);
        }
      }
      """;

    final CodeClassType classType = CodeTypes.ofClass("com.MyClass");
    check(expected, CodeClass.builder(classType)
      .addModifiers(Modifiers.PUBLIC)
      .addAnnotations(CodeAnnotation.of(JSpecifyTypes.NULL_MARKED))
      .addConstructor(CodeConstructor.builder(classType)
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
      .build()
    );
  }

  @Test
  void testGenericClass() {
    // language=java
    final String expected = """
      /// A class holding a single empty typed List for numbers.
      public class NumberListHolder<T extends Number> {
        public final List<T> list = List.of();
      }
      """;
    check(expected, CodeClass.builder("com.NumberListHolder")
      .setDocumentation(CodeDocumentation.text("A class holding a single empty typed List for numbers."))
      .addModifiers(Modifiers.PUBLIC)
      .addGenericTypes(CodeGenericTypeDefinition.of("T", GenericEnclosure.withExtends(JavaTypes.NUMBER)))
      .addFields(CodeField.builder(JavaTypes.LIST.typed(generic("T")), "list")
        .addModifiers(Modifiers.PUBLIC, Modifiers.FINAL)
        .setInitializer(JavaTypes.LIST.chainMethod("of"))
      )
      .build()
    );
  }

  @Test
  void testMethodClass() {
    // language=java
    final String expected = """
      /// Adds a value to a list, returning the list.
      @NonNull
      private static <T> List<? extends T> add(List<? extends T> list, T value) {
        list.add(value);
        return list;
      }
      """;

    final CodeType listType = JavaTypes.LIST.typed(CodeTypes.genericWildcardEnclosure(GenericEnclosure.withExtends(generic("T"))));
    check(expected, CodeMethod.builder("add")
      .setDocumentation(CodeDocumentation.text("Adds a value to a list, returning the list."))
      .addAnnotations(JSpecifyTypes.NON_NULL)
      .addModifiers(Modifiers.PRIVATE, Modifiers.STATIC)
      .addGenerics(CodeGenericTypeDefinition.of("T"))
      .setReturnType(listType)
      .addParameter(listType, "list")
      .addParameter(generic("T"), "value")
      .setCodeBlock(
        variable("list").chainMethod("add").addParameters(variable("value")),
        Statements.returnStmt(variable("list"))
      )
      .toMethod()
    );
  }
}
