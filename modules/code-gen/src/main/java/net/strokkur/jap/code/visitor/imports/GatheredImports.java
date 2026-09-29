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
package net.strokkur.jap.code.visitor.imports;

import net.strokkur.jap.code.type.CodeClassType;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GatheredImports {
  private final Set<CodeClassType> importedClasses = new HashSet<>();
  private final Set<String> importedClassNames = new HashSet<>();

  private GatheredImports() {
    // noop
  }

  public static GatheredImports empty() {
    return new GatheredImports();
  }

  public static GatheredImports of(CodeClassType type) {
    final GatheredImports out = new GatheredImports();
    out.tryImport(type);
    return out;
  }

  public static GatheredImports all() {
    return new HasAllImports();
  }

  static Collector<GatheredImports, ?, GatheredImports> collector() {
    return Collectors.reducing(GatheredImports.empty(), (to, that) -> {
      for (CodeClassType thatImport : that.importedClasses) {
        to.tryImportTopLevel(thatImport);
      }
      return to;
    });
  }

  public boolean hasImport(CodeClassType type) {
    return importedClasses.contains(type.withoutAnnotations().withoutGenerics().toTopLevel());
  }

  public boolean isNameImported(String className) {
    return importedClassNames.contains(className);
  }

  @UnmodifiableView
  public Set<CodeClassType> imports() {
    return Collections.unmodifiableSet(importedClasses);
  }

  boolean mayImport(CodeClassType type) {
    final CodeClassType topLevel = type.withoutAnnotations().withoutGenerics().toTopLevel();
    return mayImportTopLevel(topLevel);
  }

  void tryImport(CodeClassType type) {
    final CodeClassType topLevel = type.withoutAnnotations().withoutGenerics().toTopLevel();
    tryImportTopLevel(topLevel);
  }

  protected boolean mayImportTopLevel(CodeClassType type) {
    return !importedClassNames.contains(type.simpleName());
  }

  protected void tryImportTopLevel(CodeClassType topLevel) {
    if (mayImportTopLevel(topLevel)) {
      importedClasses.add(topLevel);
      importedClassNames.add(topLevel.simpleName());
    }
  }

  private static class HasAllImports extends GatheredImports {
    @Override
    public boolean hasImport(CodeClassType type) {
      return true;
    }
  }
}
