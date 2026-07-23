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

import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.documentation.AbstractDocumentationRenderer;
import net.strokkur.jap.code.documentation.MarkdownJavadocRenderer;
import net.strokkur.jap.code.documentation.StarJavadocRenderer;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePackage;
import net.strokkur.jap.code.visitor.ImportGatheringVisitor;
import net.strokkur.jap.code.visitor.source.JavaSourcePrintingVisitor;
import org.jspecify.annotations.Nullable;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class CodeGenUtil {
  private final @Nullable CodeGenProcessor processor;
  private static final ImportGatheringVisitor importVisitor = new ImportGatheringVisitor();

  public CodeGenUtil() {
    this(null);
  }

  public CodeGenUtil(@Nullable CodeGenProcessor processor) {
    this.processor = processor;
  }

  public void printJavaFile(CodeClass codeClass, Element... originatingElements) throws IOException, IllegalStateException {
    if (processor == null) {
      throw new IllegalStateException("No processor provided.");
    }

    final JavaFileObject javaFile = processor.processingEnv().getFiler().createSourceFile(
      codeClass.classType().fullyQualifiedName(),
      originatingElements
    );

    try (Writer writer = javaFile.openWriter()) {
      writer.write(createJavaFile(codeClass));
    }
  }

  public static String createJavaFile(CodeClass codeClass) {
    // The first step is to gather all imports.
    final Set<CodeClassType> imports = codeClass.accept(importVisitor);
    imports.removeIf(type -> CodePackage.isRedundantImport(codeClass.classType().codePackage(), type.codePackage()));

    final StringBuilder builder = new StringBuilder();
    builder.append("package ").append(codeClass.classType().codePackage().path()).append(";\n");
    appendImports(builder, imports);
    builder.append("\n");

    final AbstractDocumentationRenderer.Context ctx = AbstractDocumentationRenderer.createContext(
      codeClass.classType().codePackage(),
      imports
    );

    final JavaSourcePrintingVisitor printer = new JavaSourcePrintingVisitor(() -> javadocRenderer(ctx), "  ", "  ");
    builder.append(codeClass.accept(printer));
    return builder.toString();
  }

  private static AbstractDocumentationRenderer javadocRenderer(AbstractDocumentationRenderer.Context ctx) {
    if (isJava25()) {
      return new MarkdownJavadocRenderer(ctx);
    } else {
      return new StarJavadocRenderer(ctx);
    }
  }

  private static boolean isJava25() {
    try {
      SourceVersion.valueOf("RELEASE_25");
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static void appendImports(StringBuilder builder, Set<CodeClassType> imports) {
    final Map<Boolean, List<CodeClassType>> splitImports = imports.stream().sorted()
      .collect(Collectors.partitioningBy(type -> type.codePackage().path().startsWith("java")));

    // False are all non-Java imports
    if (!splitImports.get(false).isEmpty()) {
      builder.append("\n");
    }
    splitImports.get(false).forEach(type -> builder.append("import ").append(type.fullyQualifiedName()).append(";\n"));

    // True are all Java imports
    if (!splitImports.get(true).isEmpty()) {
      builder.append("\n");
    }
    splitImports.get(true).forEach(type -> builder.append("import ").append(type.fullyQualifiedName()).append(";\n"));
  }
}
