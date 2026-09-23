package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.generic.CodeGenericType;

import java.util.List;
import java.util.Objects;

record CodeArrayTypeImpl(
  CodeType inner,
  List<CodeAnnotation> annotations
) implements CodeArrayType {

  @Override
  public CodeArrayType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodeArrayTypeImpl(
      inner,
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  @Override
  public CodeGenericType toGenericType() {
    throw new IllegalStateException("Not implemented");
  }

  @Override
  public String toString() {
    return inner + "[]";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CodeArrayType that
      && that.inner().equals(this.inner)
      && that.annotations().equals(this.annotations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.inner, this.annotations);
  }
}
