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
package net.strokkur.jap.code.type;

public record CodePrimitiveType(
  String name,
  String boxedName
) implements CodeType {
  /// This cannot be used anywhere else except the return value of a method. Nothing
  /// has to specifically enforce this, but the JVM does not allow otherwise.
  public static final CodePrimitiveType VOID = new CodePrimitiveType("void", "Void");

  public static final CodePrimitiveType BYTE = new CodePrimitiveType("byte", "Byte");
  public static final CodePrimitiveType CHAR = new CodePrimitiveType("char", "Character");
  public static final CodePrimitiveType SHORT = new CodePrimitiveType("short", "Short");
  public static final CodePrimitiveType INT = new CodePrimitiveType("int", "Integer");
  public static final CodePrimitiveType LONG = new CodePrimitiveType("long", "Long");
  public static final CodePrimitiveType FLOAT = new CodePrimitiveType("float", "Float");
  public static final CodePrimitiveType DOUBLE = new CodePrimitiveType("double", "Double");
  public static final CodePrimitiveType BOOL = new CodePrimitiveType("boolean", "Boolean");

  @Override
  public String simpleName() {
    return name;
  }

  @Override
  public String fullyQualifiedName() {
    return name;
  }

  public CodeClassType boxed() {
    return CodeTypes.ofClass("java.lang." + boxedName);
  }
}
