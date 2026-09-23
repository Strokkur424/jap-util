package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;

import java.util.List;
import java.util.Objects;

record CodePrimitiveTypeImpl(
  String name,
  String boxedName,
  List<CodeAnnotation> annotations
) implements CodePrimitiveType {

  @Override
  public CodeClassType boxedType() {
    return CodeTypeImpl.createClass(
      CodeTypeImpl.createPackage("java.lang"),
      boxedName,
      List.of(),
      List.copyOf(annotations)
    );
  }

  @Override
  public CodePrimitiveType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodePrimitiveTypeImpl(
      name,
      boxedName,
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  @Override
  public CodePrimitiveType withoutAnnotations() {
    return new CodePrimitiveTypeImpl(
      name,
      boxedName,
      List.of()
    );
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CodePrimitiveType that && that.name().equals(this.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.boxedName, this.annotations);
  }
}
