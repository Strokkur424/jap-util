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

import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToMethod;
import net.strokkur.jap.code.type.CodeClassType;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface CodeDocumentation {
  void accept(DocumentationVisitor visitor);

  //<editor-fold desc="Static methods">
  static CodeDocumentation combine(CodeDocumentation... children) {
    return new DocumentationComponentList(List.of(children), false);
  }

  static CodeDocumentation combineLines(CodeDocumentation... children) {
    return new DocumentationComponentList(List.of(children), true);
  }

  static CodeDocumentation text(String text) {
    return new PlainText(text);
  }

  static CodeDocumentation header(String text, int level) {
    return new Header(text, level);
  }

  static CodeDocumentation author(String author) {
    return new Meta("author", author);
  }

  static CodeDocumentation version(String version) {
    return new Meta("version", version);
  }

  static CodeDocumentation see(ConvertToMethod method, @Nullable String description) {
    return see(method, description, null);
  }

  static CodeDocumentation see(ConvertToMethod method, @Nullable String description, @Nullable CodeClassType source) {
    return new MethodReferenceMeta("see", method.toMethod(), description, source);
  }

  static CodeDocumentation throwsMeta(ConvertToClassType exception, @Nullable String description) {
    return new ClassReferenceMeta("throws", exception.toClassType(), description);
  }

  static CodeDocumentation newline() {
    return new Newline();
  }

  /// Intended to be used inside [#combineLines(CodeDocumentation...)] for a true blank line.
  static CodeDocumentation blank() {
    return visitor -> {
      // noop
    };
  }

  static CodeDocumentation linebreak() {
    return new Linebreak();
  }

  static CodeDocumentation inlineCode(String code) {
    return new InlineCode(code);
  }

  static CodeDocumentation codeBlock(String code) {
    return new CodeBlock(code);
  }

  static CodeDocumentation url(String text, String url) {
    return new Url(text, url);
  }

  static CodeDocumentation classReference(CodeClass ref) {
    return classReference(ref, null);
  }

  static CodeDocumentation classReference(CodeClass ref, @Nullable String description) {
    return new ClassReference(ref, description);
  }

  static CodeDocumentation methodReference(ConvertToMethod ref) {
    return methodReference(ref, (String) null);
  }

  static CodeDocumentation methodReference(ConvertToMethod ref, @Nullable String description) {
    return methodReference(ref, description, null);
  }

  static CodeDocumentation methodReference(ConvertToMethod ref, @Nullable CodeClassType source) {
    return methodReference(ref, null, source);
  }

  static CodeDocumentation methodReference(ConvertToMethod ref, @Nullable String description, @Nullable CodeClassType source) {
    return new MethodReference(ref.toMethod(), description, source);
  }
  //</editor-fold>

  record DocumentationComponentList(
    List<CodeDocumentation> components,
    boolean insertNewlines
  ) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      for (CodeDocumentation component : components) {
        component.accept(visitor);
        if (insertNewlines) {
          new Newline().accept(visitor);
        }
      }
    }
  }

  record PlainText(String text) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record Meta(String descriptor, String value) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record ClassReferenceMeta(String descriptor, CodeClassType type, @Nullable String text) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record MethodReferenceMeta(
    String descriptor,
    CodeMethod codeMethod,
    @Nullable String text,
    @Nullable CodeClassType source
  ) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record Header(String text, int level) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record Newline() implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record Linebreak() implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record InlineCode(String code) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record CodeBlock(String code) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record Url(String text, String url) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record ClassReference(CodeClass codeClass, @Nullable String name) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }

  record MethodReference(CodeMethod method, @Nullable String name,
                         @Nullable CodeClassType source) implements CodeDocumentation {
    @Override
    public void accept(DocumentationVisitor visitor) {
      visitor.visit(this);
    }
  }
}
