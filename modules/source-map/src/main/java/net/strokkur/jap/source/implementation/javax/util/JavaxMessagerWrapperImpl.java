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
package net.strokkur.jap.source.implementation.javax.util;

import net.strokkur.jap.source.classmodel.SourceElement;
import net.strokkur.jap.source.implementation.javax.JavaxElement;
import net.strokkur.jap.source.util.MessagerWrapper;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public record JavaxMessagerWrapperImpl(Messager messager) implements MessagerWrapper {

  @Override
  public void print(Diagnostic.Kind kind, String format, Object... arguments) {
    messager.printMessage(kind, format.formatted(arguments));
  }

  @Override
  public void printSource(Diagnostic.Kind kind, String format, SourceElement element, Object... arguments) {
    if (element instanceof JavaxElement javax) {
      messager.printMessage(kind, format.formatted(arguments), javax.javaxElement());
    } else {
      print(kind, format, arguments);
    }
  }
}
