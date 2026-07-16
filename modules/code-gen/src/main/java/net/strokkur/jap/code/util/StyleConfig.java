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
package net.strokkur.jap.code.util;

public record StyleConfig(
  boolean newline,
  boolean multilineParameters,
  boolean newlineClosingBrace
) {
  /// Does not do any special formatting; method/field call
  /// is done right after the source expression.
  ///
  /// ```
  /// TheSource.theMethod()
  /// ```
  public static final StyleConfig DEFAULT = new StyleConfig(
    false, false, false
  );

  /// Puts the continuation (dot) on a new line, while appending
  /// one continuation indent relative to the previous level.
  ///
  /// ```
  /// TheSource
  ///   .theMethod()
  /// ```
  public static final StyleConfig NEWLINE = new StyleConfig(
    true, false, false
  );

  /// Makes method call parameters appear on separate lines.
  ///
  /// ```
  /// TheSource.theMethod(
  ///   "arg1",
  ///   "arg2"
  /// )
  /// ```
  public static final StyleConfig MULTILINE = new StyleConfig(
    false, true, false
  );

  /// Makes the method call and its parameters appear on separate lines.
  ///
  /// ```
  /// TheSource
  ///   .theMethod(
  ///     "arg1",
  ///     "arg2"
  ///   )
  /// ```
  public static final StyleConfig NEWLINE_MULTILINE = new StyleConfig(
    true, true, false
  );

  /// Makes the method call and the closing bracket appear on a new line.
  ///
  /// ```
  /// TheSource
  ///   .theMethod(arg1
  ///   )
  /// ```
  public static final StyleConfig NEWLINE_BOTH = new StyleConfig(
    true, false, true
  );
}
