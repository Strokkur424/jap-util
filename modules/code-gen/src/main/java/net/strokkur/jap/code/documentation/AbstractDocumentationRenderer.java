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

import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePackage;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.SequencedCollection;
import java.util.Set;

/// @apiNote instances of this class cannot be reused
public abstract class AbstractDocumentationRenderer implements DocumentationVisitor {
  protected final StringBuilder builder = new StringBuilder();
  protected final @Nullable CodePackage currentPath;
  protected final @Nullable @Unmodifiable Set<CodeClassType> existingImports;

  public AbstractDocumentationRenderer() {
    this(emptyContext());
  }

  public AbstractDocumentationRenderer(Context context) {
    this.currentPath = context.currentPath();
    this.existingImports = context.existingImports() != null ? Set.copyOf(context.existingImports()) : Set.of();
  }

  public static Context createContext(
    @Nullable CodePackage currentPath,
    @Nullable Set<CodeClassType> existingImports
  ) {
    return new Context(currentPath, existingImports);
  }

  public static Context emptyContext() {
    return createContext(null, null);
  }

  public abstract SequencedCollection<String> getLines();

  public record Context(
    @Nullable CodePackage currentPath,
    @Nullable Set<CodeClassType> existingImports
  ) {
  }
}
