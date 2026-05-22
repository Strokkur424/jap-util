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
import net.strokkur.jap.code.documentation.MarkdownJavadocRenderer;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.ImportGatheringVisitor;
import net.strokkur.jap.code.visitor.source.JavaSourcePrintingVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

abstract class AbstractGenTest {
  protected final void check(Set<? extends ConvertToClassType> imports, String code, CodeVisitable ast) {
    checkCode(code, ast);
    checkImports(imports, ast);
  }

  protected final void checkCode(String expected, CodeVisitable ast) {
    final JavaSourcePrintingVisitor visitor = new JavaSourcePrintingVisitor(MarkdownJavadocRenderer::new, "  ", "   ");
    final String actual = ast.accept(visitor).toString();
    assertEquals(expected, actual);
  }

  protected final void checkImports(Set<? extends ConvertToClassType> expectedSet, CodeVisitable ast) {
    final Set<CodeClassType> packages = ast.accept(new ImportGatheringVisitor());
    final Set<String> imports = packages.stream().map(CodeType::fullyQualifiedName).collect(Collectors.toSet());

    if (expectedSet.isEmpty()) {
      assertEquals(0, imports.size());
      return;
    }

    final Set<String> expected = expectedSet.stream()
      .map(type -> type.toClassType().fullyQualifiedName())
      .collect(Collectors.toSet());
    assertLinesMatch(expected.stream().sorted(), imports.stream().sorted());
  }

  protected final <T> void constructReflectively(Class<T> type) throws Throwable {
    final Constructor<T> ctor = type.getDeclaredConstructor();
    ctor.setAccessible(true);
    try {
      ctor.newInstance();
    } catch (InvocationTargetException ex) {
      throw ex.getTargetException();
    }
  }
}
