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

import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.convert.ConvertToClassType;
import net.strokkur.jap.code.type.generics.CodeGenericTypeDeclaration;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.type.CodeTypes.generic;

class ClassGenTests extends AbstractGenTest {

  @Test
  void testFullClass() {
    // language=java
    final String expectedCode = """
      /// A class holding a single empty, typed List for our custom type.
      @NonNull
      public final class ListHolder<T extends @NonNull CustomType> {
        private static final int VALUE = 2;
      
        private final List<T> list;
        private final @NonNull String @Nullable [] someArray = null;
        private final List<@Nullable String> anotherList = List.of();
      
        public static int getValue() {
          return VALUE;
        }
      
        public ListHolder() {
          this.list = List.of();
        }
      
        public List<T> getList() {
          return this.list;
        }

        @Override
        public @NonNull String @Nullable [] getSomeArray() {
          return this.someArray;
        }
      }
      """;

    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      TestTypes.LIST_HOLDER,
      TestTypes.CUSTOM_TYPE,
      JavaTypes.LIST,
      JSpecifyTypes.NON_NULL,
      JSpecifyTypes.NULLABLE,
      JavaTypes.OVERRIDE,
      JavaTypes.STRING
    );

    final CodeVisitable ast = CodeClass.builder(TestTypes.LIST_HOLDER)
      .setDocumentation(CodeDocumentation.text("A class holding a single empty, typed List for our custom type."))
      .addAnnotations(JSpecifyTypes.NON_NULL)
      .addModifiers(Modifiers.PUBLIC, Modifiers.FINAL)
      .addGenericTypes(CodeGenericTypeDeclaration.ofExtends("T", TestTypes.CUSTOM_TYPE.withAnnotations(JSpecifyTypes.NON_NULL)))

      .addFields(CodeField.builder(CodePrimitiveType.INT, "VALUE")
        .addModifiers(Modifiers.PRIVATE, Modifiers.STATIC, Modifiers.FINAL)
        .setInitializer(Expressions.intExpr(2))
      )

      .addFields(CodeField.builder(JavaTypes.LIST.typed(generic("T")), "list")
        .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
      )

      .addFields(CodeField.builder(JavaTypes.STRING.withAnnotations(JSpecifyTypes.NON_NULL).toArray().withAnnotations(JSpecifyTypes.NULLABLE), "someArray")
        .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
        .setInitializer(Expressions.nullExpr())
      )

      .addFields(CodeField.builder(JavaTypes.LIST.typed(JavaTypes.STRING.withAnnotations(JSpecifyTypes.NULLABLE)), "anotherList")
        .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
        .setInitializer(JavaTypes.LIST.chainMethod("of"))
      )

      .addMethods(CodeMethod.builder("getValue")
        .setReturnType(CodePrimitiveType.INT)
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
        .setCode(Statements.returnStmt(Expressions.fieldAccess("VALUE")))
      )

      .addConstructor(ctor -> ctor
        .addModifiers(Modifiers.PUBLIC)
        .setCodeBlock(
          Expressions.thisExpr().chainField("list")
            .assign(JavaTypes.LIST.chainMethod("of"))
        )
      )

      .addMethods(CodeMethod.builder("getList")
        .addModifiers(Modifiers.PUBLIC)
        .setReturnType(JavaTypes.LIST.typed(generic("T")))
        .setCode(Statements.returnStmt(Expressions.thisExpr().chainField("list")))
      )

      .addMethods(CodeMethod.builder("getSomeArray")
        .addAnnotations(JavaTypes.OVERRIDE)
        .addModifiers(Modifiers.PUBLIC)
        .setReturnType(JavaTypes.STRING.withAnnotations(JSpecifyTypes.NON_NULL).toArray().withAnnotations(JSpecifyTypes.NULLABLE))
        .setCode(Statements.returnStmt(Expressions.thisExpr().chainField("someArray")))
      )

      .toClass();

    check(expectedImports, expectedCode, ast);
  }

  @Test
  void testExtendsAndInterfaces() {
    final CodeClassType thisType = CodeTypes.of("idk.MyClass");
    final CodeClassType extendsType = CodeTypes.of("this.Extends");
    final CodeClassType implementsType1 = CodeTypes.of("this.Implements1");
    final CodeClassType implementsType2 = CodeTypes.of("this.Implements2");

    // language=java
    final String java = """
      class MyClass extends Extends implements Implements1, Implements2 {
      }
      """;

    check(
      Set.of(thisType, extendsType, implementsType1, implementsType2),
      java,
      CodeClass.builder(thisType)
        .extendsClass(extendsType)
        .implementsInterfaces(implementsType1, implementsType2)
        .toClass()
    );
  }

  @Test
  void ensureTypesMatch() {
    final CodeClassType type = CodeTypes.of("some.cool.Type");
    final CodeClass built = CodeClass.builder(type).toClass();

    Assertions.assertEquals(type, built.classType());
    Assertions.assertEquals(type, built.toType());
    Assertions.assertSame(built.toType(), built.classType());
  }
}
