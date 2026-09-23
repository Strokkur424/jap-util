package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.CodeGenericType;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public non-sealed class CodeGenericTypeImpl implements CodeGenericType {
  private final String name;
  private final List<CodeAnnotation> annotations;

  public CodeGenericTypeImpl(String name, List<CodeAnnotation> annotations) {
    this.name = name;
    this.annotations = List.copyOf(annotations);
  }

  @Override
  public CodeGenericType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodeGenericTypeImpl(
      name,
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof final CodeGenericType that
      && Objects.equals(name, that.name())
      && Objects.equals(annotations, that.annotations());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, annotations);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public @Unmodifiable List<CodeAnnotation> annotations() {
    return annotations;
  }
}
