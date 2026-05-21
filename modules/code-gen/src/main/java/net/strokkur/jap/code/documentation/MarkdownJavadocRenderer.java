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

import java.util.Arrays;
import java.util.SequencedCollection;

public class MarkdownJavadocRenderer extends StarJavadocRenderer {
  public MarkdownJavadocRenderer() {
    super();
  }

  public MarkdownJavadocRenderer(Context context) {
    super(context);
  }

  @Override
  public SequencedCollection<String> getLines() {
    return Arrays.stream(builder.toString().strip().split("\n"))
      .map(str -> str.isBlank() ? "" : " " + str)
      .map(str -> "///" + str)
      .toList();
  }

  @Override
  public void visit(CodeDocumentation.Header value) {
    builder.append('\n');
    builder.repeat("#", value.level()).append(' ');
    builder.append(value.text());
    builder.append('\n');
  }

  @Override
  public void visit(CodeDocumentation.Linebreak value) {
    // '<p>' in legacy JD; Markdown equivalent is to just keep the line empty.
  }

  @Override
  public void visit(CodeDocumentation.InlineCode value) {
    builder.append('`').append(value.code()).append('`');
  }

  @Override
  public void visit(CodeDocumentation.CodeBlock value) {
    builder.append("```\n");
    builder.append(value.code());
    builder.append("\n```");
  }

  @Override
  public void visit(CodeDocumentation.Url value) {
    builder.append('[').append(value.text()).append(']');
    builder.append('(').append(value.url()).append(')');
  }

  @Override
  public void visit(CodeDocumentation.ClassReference value) {
    if (value.name() != null) {
      builder.append('[').append(value.name()).append(']');
    }

    builder.append('[').append(getQualifiedTypeName(value.codeClass())).append(']');
  }

  @Override
  public void visit(CodeDocumentation.MethodReference value) {
    if (value.name() != null) {
      builder.append('[').append(value.name()).append(']');
    }

    builder.append('[').append(getMethodRefString(value.method(), value.source())).append(']');
  }
}
