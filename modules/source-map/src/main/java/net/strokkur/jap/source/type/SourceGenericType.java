package net.strokkur.jap.source.type;

import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.CodeTypes;

public record SourceGenericType(String name) implements SourceType {
  @Override
  public CodeType toType() {
    return CodeTypes.generic(name);
  }
}
