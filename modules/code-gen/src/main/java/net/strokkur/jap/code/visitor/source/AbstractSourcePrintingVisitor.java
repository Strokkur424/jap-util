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
package net.strokkur.jap.code.visitor.source;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.documentation.AbstractDocumentationRenderer;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.util.StyleConfig;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractSourcePrintingVisitor implements CodeVisitor<StringBuilder> {
  protected final Supplier<AbstractDocumentationRenderer> documentationRenderer;
  private final String indentString;
  private final String continuationIndentString;
  private int indentation = 0;
  private int continuationIndent = 0;

  public AbstractSourcePrintingVisitor(Supplier<AbstractDocumentationRenderer> documentationRenderer, String indentString, String continuationIndentString) {
    this.documentationRenderer = documentationRenderer;
    this.indentString = indentString;
    this.continuationIndentString = continuationIndentString;
  }

  protected final void appendIndented(Runnable run) {
    indentation++;
    run.run();
    indentation--;
  }

  protected final void appendIndentedContinuationConditional(boolean condition, Runnable run) {
    if (condition) {
      continuationIndent++;
    }
    run.run();
    if (condition) {
      continuationIndent--;
    }
  }

  protected final void appendIndentedContinuation(Runnable run) {
    continuationIndent++;
    run.run();
    continuationIndent--;
  }

  protected final StringBuilder append(Consumer<StringBuilder> run) {
    final StringBuilder builder = new StringBuilder();
    run.accept(builder);
    return builder;
  }

  protected final void appendIndent(StringBuilder builder) {
    builder.repeat(indentString, indentation).repeat(continuationIndentString, continuationIndent);
  }

  // Utility methods

  protected void appendNested(StringBuilder builder, CodeVisitable visitable) {
    builder.append(visitable.accept(this));
  }

  protected void appendParenthesesMaybe(StringBuilder builder, CodeVisitable visitable, boolean condition) {
    if (condition) {
      builder.append('(');
      builder.append(visitable.accept(this));
      builder.append(')');
    } else {
      builder.append(visitable.accept(this));
    }
  }

  protected <S extends CodeVisitable> String joining(Collection<S> nested) {
    return nested.stream()
      .map(visitable -> visitable.accept(this))
      .map(StringBuilder::toString)
      .collect(Collectors.joining(", "));
  }

  protected void printDocumentationIndented(StringBuilder builder, @Nullable CodeDocumentation documentation) {
    if (documentation != null) {
      final AbstractDocumentationRenderer visitor = documentationRenderer.get();
      documentation.accept(visitor);
      for (String line : visitor.getLines()) {
        appendIndent(builder);
        builder.append(line);
        builder.append("\n");
      }
    }
  }

  protected void printAnnotationsIndented(StringBuilder builder, List<CodeAnnotation> annotations) {
    for (CodeAnnotation annotation : annotations) {
      appendIndent(builder);
      appendNested(builder, annotation);
      builder.append("\n");
    }
  }

  protected void printModifiersIndented(StringBuilder builder, Set<Modifiers> modifiers) {
    appendIndent(builder);
    modifiers.stream()
      .sorted(Comparator.comparingInt(Modifiers::priority))
      .map(Modifiers::toString)
      .forEach(mod -> builder.append(mod).append(' '));
  }

  protected void appendMethodParametersMultiline(StringBuilder builder, List<CodeExpression> parameters) {
    appendIndentedContinuation(() -> {
      builder.append("\n");
      for (int i = 0, parametersSize = parameters.size(); i < parametersSize; i++) {
        final CodeExpression parameter = parameters.get(i);
        appendIndent(builder);
        appendNested(builder, parameter);
        if (i + 1 < parametersSize) {
          builder.append(",");
        }
        builder.append("\n");
      }
    });
    appendIndent(builder);
  }

  protected void appendMethodCallParams(StringBuilder builder, List<CodeExpression> parameters, StyleConfig style) {
    builder.append("(");
    appendIndentedContinuation(() -> {
      if (style.multilineParameters()) {
        appendMethodParametersMultiline(builder, parameters);
      } else {
        builder.append(joining(parameters));
      }

      if (style.newlineClosingBrace()) {
        builder.append("\n");
        appendIndent(builder);
      }
    });
    builder.append(")");
  }
}
