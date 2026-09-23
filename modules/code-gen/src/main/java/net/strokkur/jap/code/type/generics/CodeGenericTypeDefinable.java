package net.strokkur.jap.code.type.generics;

import net.strokkur.jap.code.type.convert.ConvertToGenericTypeDefinable;
import net.strokkur.jap.code.visitor.CodeVisitable;

/// Marker interface for anything that can be used when you define the generic types of a
/// type. This specifically refers to the usage of a type (.e.g `List<String> variable`).
public sealed interface CodeGenericTypeDefinable
  extends CodeVisitable, ConvertToGenericTypeDefinable
  permits CodeEnclosable, CodeWildcard {

  @Override
  default CodeGenericTypeDefinable toGenericTypeDefinable() {
    return this;
  }
}
