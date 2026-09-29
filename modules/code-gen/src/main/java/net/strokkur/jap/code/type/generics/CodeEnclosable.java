package net.strokkur.jap.code.type.generics;

import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeGenericType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.convert.ConvertToEnclosable;
import net.strokkur.jap.code.visitor.CodeVisitable;

/// Marker interface for anything that can be enclosed as a generic type.
public sealed interface CodeEnclosable
  extends CodeVisitable, CodeType, ConvertToEnclosable, CodeGenericTypeDefinable
  permits CodeClassType, CodeArrayType, CodeGenericType {

  @Override
  default CodeEnclosable toEnclosable() {
    return this;
  }
}
