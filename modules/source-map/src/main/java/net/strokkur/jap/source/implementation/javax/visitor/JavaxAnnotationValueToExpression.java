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
package net.strokkur.jap.source.implementation.javax.visitor;

import net.strokkur.jap.code.expression.CodeExpression;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class JavaxAnnotationValueToExpression implements AnnotationValueVisitor<CodeExpression, Void> {
  public static JavaxAnnotationValueToExpression VISITOR = new JavaxAnnotationValueToExpression();

  @Override
  public CodeExpression visit(AnnotationValue av, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitBoolean(boolean b, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitByte(byte b, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitChar(char c, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitDouble(double d, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitFloat(float f, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitInt(int i, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitLong(long i, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitShort(short s, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitString(String s, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitType(TypeMirror t, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitEnumConstant(VariableElement c, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitAnnotation(AnnotationMirror a, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitArray(List<? extends AnnotationValue> vals, Void unused) {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public CodeExpression visitUnknown(AnnotationValue av, Void unused) {
    throw new IllegalStateException("Not implemented");
  }
}
