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
package net.strokkur.jap.code.type;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record CodePackage(String[] paths) implements Comparable<CodePackage> {
  private static final CodePackage JAVA_LANG = new CodePackage(new String[]{"java", "lang"});

  public static CodePackage of(String packageString) {
    return new CodePackage(packageString.split("\\."));
  }

  public static CodePackage of(List<String> packages) {
    return new CodePackage(packages.toArray(String[]::new));
  }

  public String path() {
    return String.join(".", paths);
  }

  public static boolean isRedundantImport(@Nullable CodePackage maybeRoot, CodePackage other) {
    return other.equals(JAVA_LANG) || Objects.equals(maybeRoot, other);
  }

  @Override
  public int compareTo(CodePackage o) {
    return path().compareTo(o.path());
  }
}
