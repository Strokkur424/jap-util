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
package net.strokkur.jap.code.test.type;

import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TypeTests {

  @Test
  void testOfClassParsing() {
    final CodeClassType type$1 = CodeTypes.ofClass("org.bukkit.attribute.Attribute");
    assertEquals("Attribute", type$1.simpleName());
    assertEquals("Attribute", type$1.name());
    assertEquals("org.bukkit.attribute", type$1.codePackage().path());
    assertEquals("org.bukkit.attribute.Attribute", type$1.identifiableName());
    assertEquals("org.bukkit.attribute.Attribute", type$1.fullyQualifiedName());

    final CodeClassType type$2 = CodeTypes.ofClass("org.bukkit.entity.Cow$Variant");
    assertEquals("Cow.Variant", type$2.simpleName());
    assertEquals("Variant", type$2.name());
    assertEquals("org.bukkit.entity", type$2.codePackage().path());
    assertEquals("org.bukkit.entity.Cow$Variant", type$2.identifiableName());
    assertEquals("org.bukkit.entity.Cow.Variant", type$2.fullyQualifiedName());
  }

  @Test
  void testEquals() {
    final CodeClassType cowVariant = CodeTypes.ofClass("org.bukkit.entity.Cow$Variant");
    assertNotEquals(CodeTypes.ofClass("org.bukkit.entity.Cow"), cowVariant);
    assertNotEquals(CodeTypes.ofClass("org.bukkit.entity.Variant"), cowVariant);
    assertEquals(CodeTypes.ofClass("org.bukkit.entity.Cow$Variant"), cowVariant);
    assertNotEquals(CodeTypes.ofClass("org.wrong.entity.Cow$Variant"), cowVariant);
  }
}
