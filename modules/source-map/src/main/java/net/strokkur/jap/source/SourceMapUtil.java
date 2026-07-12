/*
 * This file is part of source-map, licensed under the MIT License.
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
package net.strokkur.jap.source;

import net.strokkur.jap.source.classmodel.SourceClassLike;
import net.strokkur.jap.source.classmodel.SourceConstructor;
import net.strokkur.jap.source.classmodel.SourceElement;
import net.strokkur.jap.source.classmodel.SourceField;
import net.strokkur.jap.source.classmodel.SourceMethod;
import net.strokkur.jap.source.implementation.javax.ElementUtil;
import net.strokkur.jap.source.implementation.javax.JavaxConstructor;
import net.strokkur.jap.source.implementation.javax.JavaxUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class SourceMapUtil {
  private final SourceMapProcessor processor;

  public SourceMapUtil(SourceMapProcessor processor) {
    this.processor = processor;
  }

  public SourceElement parseElement(Element element) {
    return switch (element) {
      case TypeElement typeElement -> parseClassElement(typeElement);
      case ExecutableElement executableElement -> {
        if (executableElement.getKind() == ElementKind.CONSTRUCTOR) {
          yield parseConstructorElement(executableElement);
        } else {
          yield parseMethodElement(executableElement);
        }
      }
      case VariableElement variableElement -> {
        if (element.getKind() == ElementKind.FIELD) {
          yield parseFieldElement(variableElement);
        }
        throw new IllegalArgumentException("Cannot convert element of type: " + element.getKind());
      }
      default -> throw new IllegalArgumentException("Cannot convert element of type: " + element.getKind());
    };
  }

  public SourceClassLike parseClassElement(TypeElement element) {
    return ElementUtil.getClassLikeFor(processor, element);
  }

  public SourceMethod parseMethodElement(ExecutableElement element) {
    return JavaxUtil.convertMethod(processor, element);
  }

  public SourceConstructor parseConstructorElement(ExecutableElement element) {
    return JavaxUtil.convertConstructor(processor, element);
  }

  public SourceField parseFieldElement(VariableElement element) {
    return JavaxUtil.convertField(processor, element);
  }

}
