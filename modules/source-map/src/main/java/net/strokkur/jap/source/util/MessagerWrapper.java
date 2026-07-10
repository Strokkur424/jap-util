/*
 * This file is part of source-map, licensed under the MIT License.
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
package net.strokkur.jap.source.util;

import net.strokkur.jap.source.classmodel.SourceElement;
import net.strokkur.jap.source.implementation.javax.util.JavaxMessagerWrapperImpl;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;

public interface MessagerWrapper {

  String DEBUG_SYSTEM_PROPERTY = "japutil.debug";

  static MessagerWrapper wrap(Messager messager) {
    return new JavaxMessagerWrapperImpl(messager);
  }

  void print(Kind kind, String format, Object... arguments);

  void printSource(Kind kind, String format, SourceElement element, Object... arguments);

  /**
   * Prints a formatted message to the {@link Kind#OTHER} channel.
   * <p>
   * The message is silently discarded unless the system property {@code -Dstrokk.command.debug} is set.
   */
  default void debug(String format, Object... args) {
    if (System.getProperty(DEBUG_SYSTEM_PROPERTY) != null) {
      print(Kind.OTHER, format, args);
    }
  }

  /**
   * Prints a formatted message about this element to the {@link Kind#OTHER} channel.
   * <p>
   * The message is silently discarded unless the system property {@code -Dstrokk.command.debug} is set.
   */
  default void debugSource(String format, SourceElement element, Object... args) {
    if (System.getProperty(DEBUG_SYSTEM_PROPERTY) != null) {
      printSource(Kind.OTHER, format, element, args);
    }
  }

  /**
   * Prints a formatted message to the {@link Kind#NOTE} channel.
   */
  default void info(String format, Object... arguments) {
    print(Kind.NOTE, format, arguments);
  }

  /**
   * Prints a formatted message about this element to the {@link Kind#NOTE} channel.
   */
  default void infoSource(String format, SourceElement element, Object... arguments) {
    printSource(Kind.NOTE, format, element, arguments);
  }

  /**
   * Prints a formatted message to the {@link Kind#WARNING} channel.
   */
  default void warn(String format, Object... arguments) {
    print(Kind.WARNING, format, arguments);
  }

  /**
   * Prints a formatted message about this element to the {@link Kind#WARNING} channel.
   */
  default void warnSource(String format, SourceElement element, Object... arguments) {
    printSource(Kind.WARNING, format, element, arguments);
  }

  /**
   * Prints a formatted message to the {@link Kind#ERROR} channel.
   */
  default void error(String format, Object... arguments) {
    print(Kind.ERROR, format, arguments);
  }

  /**
   * Prints a formatted message about this element to the {@link Kind#ERROR} channel.
   */
  default void errorSource(String format, SourceElement element, Object... arguments) {
    printSource(Kind.ERROR, format, element, arguments);
  }
}
