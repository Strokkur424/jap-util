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

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodeAnnotationType;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

class AnnotationTypeGenTests extends AbstractGenTest {

  @Test
  void testFullAnnotationType() {
    //language=Java
    final String code = """
      @Retention(RetentionPolicy.CLASS)
      @interface MyAnnotationType {
        String DEFAULT_NAME = "Deez";
      
        String name() default DEFAULT_NAME;
      
        /// The age of this element, in days.
        int age() default 18;
      }
      """;

    final Set<? extends ConvertToClassType> imports = Set.of(
      TestTypes.MY_ANNOTATION_TYPE,
      CodeTypes.of(Retention.class),
      CodeTypes.of(RetentionPolicy.class),
      JavaTypes.STRING
    );

    check(imports, code, CodeAnnotationType.builder(TestTypes.MY_ANNOTATION_TYPE)
      .addAnnotations(CodeAnnotation.of(CodeTypes.of(Retention.class), CodeTypes.of(RetentionPolicy.class).chainField("CLASS")))

      .addFields(
        CodeField.builder(JavaTypes.STRING, "DEFAULT_NAME").setInitializer(Expressions.string("Deez"))
      )
      .addMethods(
        CodeMethod.builder("name")
          .setReturnType(JavaTypes.STRING)
          .withDefault(Expressions.variable("DEFAULT_NAME")),
        CodeMethod.builder("age")
          .setDocumentation(CodeDocumentation.text("The age of this element, in days."))
          .setReturnType(CodePrimitiveType.INT)
          .withDefault(Expressions.intExpr(18))
      )

      .toAnnotationType()
    );
  }
}
