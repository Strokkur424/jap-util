package net.strokkur.jap.code.test.type;

import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.CodeTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TypeTests {

  @Test
  void testOfClassParsing() {
    CodeClassType type$1 = CodeTypes.ofClass("org.bukkit.attribute.Attribute");
    assertEquals("Attribute", type$1.simpleName());
    assertEquals("Attribute", type$1.name());
    assertEquals("org.bukkit.attribute", type$1.codePackage().path());
    assertEquals("org.bukkit.attribute.Attribute", type$1.identifiableName());
    assertEquals("org.bukkit.attribute.Attribute", type$1.fullyQualifiedName());

    CodeClassType type$2 = CodeTypes.ofClass("org.bukkit.entity.Cow$Variant");
    assertEquals("Cow.Variant", type$2.simpleName());
    assertEquals("Variant", type$2.name());
    assertEquals("org.bukkit.entity", type$2.codePackage().path());
    assertEquals("org.bukkit.entity.Cow$Variant", type$2.identifiableName());
    assertEquals("org.bukkit.entity.Cow.Variant", type$2.fullyQualifiedName());
  }

  @Test
  void testEquals() {
    CodeClassType cowVariant = CodeTypes.ofClass("org.bukkit.entity.Cow$Variant");
    assertNotEquals(CodeTypes.ofClass("org.bukkit.entity.Cow"), cowVariant);
    assertNotEquals(CodeTypes.ofClass("org.bukkit.entity.Variant"), cowVariant);
    assertEquals(CodeTypes.ofClass("org.bukkit.entity.Cow$Variant"), cowVariant);
    assertNotEquals(CodeTypes.ofClass("org.wrong.entity.Cow$Variant"), cowVariant);
  }
}
