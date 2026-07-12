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
package net.strokkur.jap.source.implementation.javax;

import com.sun.source.tree.VariableTree;
import net.strokkur.jap.source.SourceMapProcessor;
import net.strokkur.jap.source.classmodel.SourceConstructor;
import net.strokkur.jap.source.classmodel.SourceField;
import net.strokkur.jap.source.classmodel.SourceMethod;
import net.strokkur.jap.source.classmodel.SourceMethodParameter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public final class JavaxUtil {

  public static SourceMethodParameter convertParameter(SourceMapProcessor processor, VariableElement element) {
    return new JavaxMethodParameter(
      element,
      ElementUtil.mapAnnotations(processor, element),
      ElementUtil.mapType(processor, element.asType()),
      element.getSimpleName().toString()
    );
  }

  public static SourceMethod convertMethod(SourceMapProcessor processor, ExecutableElement element) {
    return new JavaxMethod(
      element,
      ElementUtil.getClassLikeFor(processor, (TypeElement) element.getEnclosingElement()),
      ElementUtil.mapAnnotations(processor, element),
      ElementUtil.mapModifiers(element.getModifiers()),
      element.getParameters().stream().map(e -> convertParameter(processor, e)).toList(),
      element.getThrownTypes().stream().map(type -> ElementUtil.mapType(processor, type)).toList(),
      ElementUtil.mapType(processor, element.getReturnType()),
      element.getSimpleName().toString()
    );
  }

  public static SourceConstructor convertConstructor(SourceMapProcessor processor, ExecutableElement element) {
    return new JavaxConstructor(
      element,
      ElementUtil.getClassLikeFor(processor, (TypeElement) element.getEnclosingElement()),
      ElementUtil.mapAnnotations(processor, element),
      ElementUtil.mapModifiers(element.getModifiers()),
      element.getParameters().stream().map(e -> convertParameter(processor, e)).toList(),
      element.getThrownTypes().stream().map(type -> ElementUtil.mapType(processor, type)).toList()
    );
  }

  public static SourceField convertField(SourceMapProcessor processor, VariableElement element) {
    return new JavaxField(
      element,
      ElementUtil.getClassLikeFor(processor, (TypeElement) element.getEnclosingElement()),
      ElementUtil.mapAnnotations(processor, element),
      ElementUtil.mapModifiers(element.getModifiers()),
      ElementUtil.mapType(processor, element.asType()),
      element.getSimpleName().toString(),
      ElementUtil.toLazyMirror(((VariableTree) processor.trees().getTree(element)).getInitializer())
    );
  }

  private JavaxUtil() throws IllegalAccessError {
    throw new IllegalAccessError("This class cannot be instantiated.");
  }
}
