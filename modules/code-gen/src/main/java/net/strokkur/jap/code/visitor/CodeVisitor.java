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
package net.strokkur.jap.code.visitor;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.annotations.CodeAnnotationParameter;
import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeConstructor;
import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.statement.CodeStatement;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;

public interface CodeVisitor<R> {
  R visitAnnotation(CodeAnnotation annotation);

  R visitAnnotationParameter(CodeAnnotationParameter annotationParameter);

  R visitClass(CodeClass codeClass);

  R visitConstructor(CodeConstructor ctor);

  R visitField(CodeField field);

  R visitMethod(CodeMethod method);

  R visitParameterDefinition(CodeParameterDefinition parameter);

  R visitExpression(CodeExpression expression);

  R visitStatement(CodeStatement statement);

  R visitType(CodeType type);

  R visitCodeBlock(CodeBlock block);

  R visitGenericTypeDefinition(CodeGenericTypeDefinition genericTypeDefinition);

  R visitGenericEnclosure(GenericEnclosure enclosure);
}
