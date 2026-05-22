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
package net.strokkur.jap.code;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.annotations.CodeAnnotationParameter;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.ImportGatheringVisitor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

class ImportGatherTests {

  private void check(String expectedMultiline, CodeVisitable visitable) {
    final Set<CodeClassType> packages = visitable.accept(new ImportGatheringVisitor());
    final Set<String> imports = packages.stream().map(CodeType::fullyQualifiedName).collect(Collectors.toSet());

    if (expectedMultiline.isBlank()) {
      assertEquals(0, imports.size());
      return;
    }

    final Set<String> expected = Arrays.stream(expectedMultiline.strip().split("\n"))
      .collect(Collectors.toSet());
    assertLinesMatch(expected.stream().sorted(), imports.stream().sorted());
  }

  @Test
  void testGatherExpressionImports() {
    // Test static method invocation
    check("com.example.Test",
      CodeTypes.ofClass("com.example.Test").chainMethod("execute")
    );

    // Test static method invocation with parameters
    check("""
      com.example.Test
      io.library.Value
      """, CodeTypes.ofClass("com.example.Test")
      .chainMethod("execute")
      .addParameters(
        Expressions.ctorInvocation(CodeTypes.ofClass("io.library.Value"))
      )
    );

    // Test constructor invocation with variable source
    check("com.CtorClass", Expressions.ctorInvocation(
      CodeTypes.ofClass("com.CtorClass")
    ).setSource(Expressions.variable("variable")));

    // Test instance method invocation
    check("", Expressions.thisExpr().chainMethod("execute"));

    // Test instance method invocation with parameters
    check(
      "io.library.Value",
      Expressions.thisExpr().chainMethod("execute")
        .addParameters(Expressions.ctorInvocation(CodeTypes.ofClass("io.library.Value")))
    );

    // Test method reference
    check(
      "io.library.Example",
      Expressions.methodReference(CodeTypes.ofClass("io.library.Example"), "run")
    );
  }

  @Test
  void testFields() {
    // No initializer
    check("java.lang.String",
      CodeField.builder(JavaTypes.STRING, "field").toField()
    );

    // With initializer
    check("""
      java.lang.String
      com.example.TestClass
      """, CodeField.builder(JavaTypes.STRING, "field")
      .setInitializer(CodeTypes.ofClass("com.example.TestClass").chainMethod("get"))
      .toField()
    );
  }

  @Test
  void testStatements() {
    // Variable declaration (no init)
    check("""
      java.lang.String
      """, Statements.variableDeclaration(JavaTypes.STRING, "name", null));

    // Variable declaration (with init)
    check("""
        java.lang.String
        com.example.TheClass
        """,
      Statements.variableDeclaration(
        JavaTypes.STRING,
        "name",
        CodeTypes.ofClass("com.example.TheClass").chainMethod("get")
      )
    );

    // Return statement (no value)
    check("", Statements.returnStmt(null));
    // Return statement (with value)
    check("", Statements.returnStmt(Expressions.nullExpr()));

    // Throw statement
    check("java.lang.NullPointerException",
      Statements.throwStatement(
        Expressions.ctorInvocation(JavaTypes.NULL_POINTER_EXCEPTION)
          .addParameters(Expressions.string("It was null :("))
      )
    );

    // Method invocation (instance)
    check("", Expressions.methodInvocation("doSomething")
      .addParameters(Expressions.string("test"))
    );
    // Method invocation (static)
    check("io.declared.ThisClass",
      CodeTypes.ofClass("io.declared.ThisClass").chainMethod("doSomething")
        .addParameters(Expressions.string("test"))
    );
  }

  @Test
  void testArray() {
    check("io.library.TestClass", CodeTypes.ofClass("io.library.TestClass").toArray());
    check("java.lang.String", JavaTypes.STRING.toArray());
    // String[][]
    check("java.lang.String", JavaTypes.STRING.toArray().toArray());
  }

  @Test
  void testLambda() {
    check("io.library.SomeTest", Expressions.lambdaInline(
      CodeTypes.ofClass("io.library.SomeTest").chainMethod("do")
    ));
    check("""
        io.library.SomeTest
        io.library.AnotherTest
        """,
      Expressions.lambda(
        CodeTypes.ofClass("io.library.SomeTest").chainMethod("do"),
        CodeTypes.ofClass("io.library.AnotherTest").chainMethod("anotherOne")
      )
    );
  }

  @Test
  void testIfStatement() {
    // With instanceof
    check("org.bukkit.entity.Player", Statements.ifStmt(
      Expressions.instanceOf(
        Expressions.variable("ctx").chainMethod("getSource"),
        CodeTypes.ofClass("org.bukkit.entity.Player"),
        "source"
      ),
      Expressions.variable("source").chainMethod("sendPlainMessage")
        .addParameters(Expressions.string("Hello!"))
        .toStatement()
    ));

    check("""
      java.lang.IllegalStateException
      io.library.StaticLibrary
      org.bukkit.entity.Player
      """, Statements.ifStmt(
      Expressions.instanceOf(
        CodeTypes.ofClass("io.library.StaticLibrary").chainMethod("getSource"),
        CodeTypes.ofClass("org.bukkit.entity.Player"),
        null
      ).invert(),
      Statements.throwStatement(Expressions.ctorInvocation(JavaTypes.ILLEGAL_STATE_EXCEPTION)
        .addParameters(Expressions.string("Don't do that."))
      ))
    );
  }

  @Test
  void testFieldAccess() {
    // None of these have any imports
    check("", Expressions.fieldAccess("value"));
    check("", Expressions.variable("inst").chainField("value"));

    // These should fetch the same imports as the source expr (including static types)
    check("some.ClassType", CodeTypes.ofClass("some.ClassType").chainField("value"));
    check("another.ClassType", CodeTypes.ofClass("another.ClassType")
      .chainMethod("fetch")
      .chainField("value")
    );

    // They can be nested!
    check("yet.Yet", CodeTypes.ofClass("yet.Yet")
      .chainField("another")
      .chainField("value")
    );
  }

  @Test
  void testAnnotations() {
    check("org.jspecify.annotations.Nullable", CodeField.builder(CodePrimitiveType.INT, "value")
      .addAnnotations(CodeAnnotation.of(
        CodeTypes.ofClass("org.jspecify.annotations.Nullable")
      ))
      .toField()
    );
    check("valued.annotations.ValueType", CodeField.builder(CodePrimitiveType.INT, "value")
      .addAnnotations(CodeAnnotation.of(
        CodeTypes.ofClass("valued.annotations.ValueType"),
        Expressions.string("some cool value")
      ))
      .toField()
    );
    check("valued.annotations.ValueType", CodeField.builder(CodePrimitiveType.INT, "value")
      .addAnnotations(CodeAnnotation.of(
        CodeTypes.ofClass("valued.annotations.ValueType"),
        CodeAnnotationParameter.of("name", Expressions.string("some cool value")),
        CodeAnnotationParameter.of("ordinal", Expressions.intExpr(2))
      ))
      .toField()
    );
  }

  @Test
  void testTyped() {
    check("""
      java.util.List
      java.lang.String""", JavaTypes.LIST.typed(JavaTypes.STRING));
  }

  @Test
  void testWithGenerics() {
    check("""
      java.lang.String
      java.util.List
      annotations.CustomAnnotations""", CodeMethod.builder("runit")
      .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
      .addGenerics(
        CodeGenericTypeDefinition.of("S"),
        CodeGenericTypeDefinition.of("T", GenericEnclosure.withExtends(JavaTypes.STRING))
      )
      .addParameters(CodeParameterDefinition.of(
        JavaTypes.LIST.typed(CodeTypes.generic("T")),
        "tList",
        CodeAnnotation.of(CodeTypes.ofClass("annotations.CustomAnnotations"))
      ))
      .setCodeBlock()
      .toMethod()
    );
  }

  @Test
  void testBlank() {
    check("", Statements.blank());
  }
}
