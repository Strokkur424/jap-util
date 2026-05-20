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
package net.strokkur.jap.code.documentation;

import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePackage;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SequencedCollection;
import java.util.stream.Collectors;

public class StarJavadocRenderer extends AbstractDocumentationRenderer {
  public StarJavadocRenderer(Context context) {
    super(context);
  }

  @Override
  public SequencedCollection<String> getLines() {
    final List<String> out = new ArrayList<>();
    out.add("/**");
    out.addAll(Arrays.stream(builder.toString().strip().split("\n"))
      .map(str -> str.isEmpty() ? " *" : " * " + str)
      .toList());
    out.add(" */");
    return out;
  }

  @Override
  public void visit(CodeDocumentation.PlainText value) {
    builder.append(value.text());
  }

  @Override
  public void visit(CodeDocumentation.Meta value) {
    builder
      .append('@')
      .append(value.descriptor())
      .append(' ')
      .append(value.value());
  }

  @Override
  public void visit(CodeDocumentation.MethodReferenceMeta value) {
    builder
      .append('@')
      .append(value.descriptor())
      .append(' ')
      .append(getMethodRefString(value.codeMethod(), value.source()));

    if (value.text() != null) {
      builder.append(' ').append(value.text());
    }
  }

  @Override
  public void visit(CodeDocumentation.ClassReferenceMeta value) {
    builder
      .append('@')
      .append(value.descriptor())
      .append(' ')
      .append(getQualifiedTypeName(value.type()));

    if (value.text() != null) {
      builder.append(' ').append(value.text());
    }
  }

  @Override
  public void visit(CodeDocumentation.Header value) {
    builder.append('\n');
    builder.append("<h").append(value.level()).append('>');
    builder.append(value.text());
    builder.append("</h").append(value.level()).append('>');
    builder.append('\n');
  }

  @Override
  public void visit(CodeDocumentation.Newline value) {
    builder.append('\n');
  }

  @Override
  public void visit(CodeDocumentation.Linebreak value) {
    builder.append("<p>");
  }

  @Override
  public void visit(CodeDocumentation.InlineCode value) {
    builder.append("{@code ").append(value.code()).append("}");
  }

  @Override
  public void visit(CodeDocumentation.CodeBlock value) {
    builder.append("<pre>{@code\n")
      .append(value.code())
      .append("\n}</pre>");
  }

  @Override
  public void visit(CodeDocumentation.Url value) {
    builder.append("<a href=\"").append(value.url()).append("\">")
      .append(value.text())
      .append("</a>");
  }

  @Override
  public void visit(CodeDocumentation.ClassReference value) {
    builder.append("{@link ").append(getQualifiedTypeName(value.codeClass().classType()));
    if (value.name() != null) {
      builder.append(" ").append(value.name());
    }
    builder.append("}");
  }

  @Override
  public void visit(CodeDocumentation.MethodReference value) {
    builder.append("{@link ").append(getMethodRefString(value.method(), value.source()));
    if (value.name() != null) {
      builder.append(" ").append(value.name());
    }
    builder.append("}");
  }

  protected String getQualifiedTypeName(CodeClassType type) {
    if (CodePackage.isRedundantImport(currentPath, type.codePackage())
      || existingImports != null && existingImports.contains(type)
    ) {
      return type.name();
    }
    return type.fullyQualifiedName();
  }

  public String javadocName(CodeMethod method) {
    return method.name() + "(" + method.parameters().stream()
      .map(param -> {
        if (param.type() instanceof CodeClassType classType) {
          return getQualifiedTypeName(classType);
        }
        return param.type().fullyQualifiedName();
      })
      .collect(Collectors.joining(", "))
      + ")";
  }

  protected String getMethodRefString(CodeMethod method, @Nullable CodeClassType from) {
    return from == null
      ? "#" + javadocName(method)
      : getQualifiedTypeName(from) + "#" + javadocName(method);
  }
}
