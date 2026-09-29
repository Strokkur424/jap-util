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

import net.strokkur.jap.code.classmodel.CodeEnum;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.type.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.expression.Expressions.string;

class EnumGenTests extends AbstractGenTest {

  @Test
  void testFullEnum() {
    //language=Java
    final String code = """
      /// Of course we have docs!
      public enum CoolPeople {
        MBAXTER("mbax", "kittens"),
        ELETRONICCAT("cat", "cursing"),
        /// I don't actually know his favorite thing, lol
        KENNYTV("german", "the chat reporting feature");
      
        private final String nickname;
        private final String favoriteThing;
      
        CoolPeople(String nickname, String favoriteThing) {
          this.nickname = nickname;
          this.favoriteThing = favoriteThing;
        }
      
        @Override
        public String toString() {
          return nickname + " likes " + favoriteThing;
        }
      }
      """;

    final Set<? extends ConvertToClassType> imports = Set.of(
      TestTypes.COOL_PEOPLE,
      JavaTypes.STRING,
      JavaTypes.OVERRIDE
    );

    check(imports, code, CodeEnum.builder(TestTypes.COOL_PEOPLE)
      .setDocumentation(CodeDocumentation.text("Of course we have docs!"))
      .addModifiers(Modifiers.PUBLIC)

      .addValue("MBAXTER", string("mbax"), string("kittens"))
      .addValue("ELETRONICCAT", string("cat"), string("cursing"))
      .addValue(
        "KENNYTV",
        CodeDocumentation.text("I don't actually know his favorite thing, lol"),
        string("german"),
        string("the chat reporting feature")
      )

      .addFields(CodeField.builder(JavaTypes.STRING, "nickname").addModifiers(Modifiers.PRIVATE, Modifiers.FINAL))
      .addFields(CodeField.builder(JavaTypes.STRING, "favoriteThing").addModifiers(Modifiers.PRIVATE, Modifiers.FINAL))

      .addConstructor(builder -> builder
        .addParameter(JavaTypes.STRING, "nickname")
        .addParameter(JavaTypes.STRING, "favoriteThing")
        .setCodeBlock(
          Expressions.thisExpr().chainField("nickname").assign(Expressions.variable("nickname")),
          Expressions.thisExpr().chainField("favoriteThing").assign(Expressions.variable("favoriteThing"))
        )
      )

      .addMethods(CodeMethod.builder("toString")
        .addAnnotations(JavaTypes.OVERRIDE)
        .addModifiers(Modifiers.PUBLIC)
        .setReturnType(JavaTypes.STRING)
        .setCode(
          Statements.returnStmt(
            Expressions.variable("nickname")
              .concat(" likes ")
              .concat(Expressions.variable("favoriteThing"))
          )
        )
      )

      .toEnum()
    );
  }
}
