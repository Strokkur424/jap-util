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
import net.strokkur.jap.code.classmodel.CodeInterface;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.util.Modifiers;
import org.junit.jupiter.api.Test;

import java.util.Set;

class InterfaceGenTests extends AbstractGenTest {

  @Test
  void testFullInterface() {
    //language=Java
    final String code = """
      @NullUnmarked
      public interface Triple<L, M, R> extends Double<@Nullable L, R> {
        Triple<@Nullable Void, Void, Void> EMPTY = new EmptyTriple();
      
        L left();
      
        @Nullable M middle();
      
        R right();
      }
      """;

    final CodeClassType VOID = CodeTypes.of(Void.class);
    final Set<? extends ConvertToClassType> imports = Set.of(
      JSpecifyTypes.NULL_UNMARKED,
      JSpecifyTypes.NULLABLE,
      TestTypes.TRIPLE,
      TestTypes.EMPTY_TRIPPLE,
      TestTypes.DOUBLE,
      VOID
    );

    check(imports, code, CodeInterface.builder(TestTypes.TRIPLE)
      .addAnnotations(JSpecifyTypes.NULL_UNMARKED)
      .addModifiers(Modifiers.PUBLIC)
      .addGenericTypes(
        CodeGenericTypeDefinition.of("L"),
        CodeGenericTypeDefinition.of("M"),
        CodeGenericTypeDefinition.of("R")
      )
      .extendsInterfaces(
        TestTypes.DOUBLE.typed(CodeTypes.generic("L").withAnnotations(JSpecifyTypes.NULLABLE), CodeTypes.generic("R"))
      )

      .addFields(
        CodeField.builder(
          TestTypes.TRIPLE.typed(VOID.withAnnotations(JSpecifyTypes.NULLABLE), VOID, VOID),
          "EMPTY"
        ).setInitializer(TestTypes.EMPTY_TRIPPLE.ctor())
      )
      .addMethods(
        CodeMethod.builder("left").setReturnType(CodeTypes.generic("L")),
        CodeMethod.builder("middle").setReturnType(CodeTypes.generic("M").withAnnotations(JSpecifyTypes.NULLABLE)),
        CodeMethod.builder("right").setReturnType(CodeTypes.generic("R"))
      )

      .toInterface()
    );
  }
}
