package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.convert.ConvertToType;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

public non-sealed class CodeArrayTypeImpl implements CodeArrayType {
  private final CodeType inner;
  private final List<CodeAnnotation> annotations;

  protected CodeArrayTypeImpl(CodeType inner, List<CodeAnnotation> annotations) {
    this.inner = inner;
    this.annotations = annotations;
  }

  @Override
  public CodeArrayType withInner(ConvertToType inner) {
    return new CodeArrayTypeImpl(
      inner.toType(),
      annotations()
    );
  }

  @Override
  public CodeArrayType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodeArrayTypeImpl(
      inner(),
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  @Override
  public String toString() {
    return inner() + "[]";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CodeArrayType that
      && that.inner().equals(this.inner())
      && that.annotations().equals(this.annotations());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.inner(), this.annotations());
  }

  @Override
  public CodeType inner() {
    return this.inner;
  }

  @Override
  public @Unmodifiable List<CodeAnnotation> annotations() {
    return annotations;
  }
}
