package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodePackage;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.convert.ConvertToGenericTypeDefinable;
import net.strokkur.jap.code.type.generics.CodeGenericTypeDefinable;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public non-sealed class CodeClassTypeImpl implements CodeClassType {
  private final CodePackage codePackage;
  private final String simpleName;
  private final @Nullable List<CodeGenericTypeDefinable> genericTypes;
  private final List<CodeAnnotation> annotations;

  protected CodeClassTypeImpl(
    CodePackage codePackage,
    String simpleName,
    @Nullable List<CodeGenericTypeDefinable> genericTypes,
    List<CodeAnnotation> annotations
  ) {
    this.codePackage = codePackage;
    this.simpleName = simpleName;
    this.genericTypes = genericTypes;
    this.annotations = annotations;
  }

  @Override
  public CodeClassType withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodeClassTypeImpl(
      codePackage,
      simpleName,
      genericTypes,
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  public CodeClassType toTopLevel() {
    return new CodeClassTypeImpl(
      codePackage,
      simpleName.split("\\.", 2)[0],
      genericTypes,
      annotations
    );
  }

  @Override
  public CodeClassType withoutGenerics() {
    return new CodeClassTypeImpl(
      codePackage,
      simpleName,
      null,
      annotations
    );
  }

  @Override
  public CodeClassType typed(List<? extends ConvertToGenericTypeDefinable> types) {
    return new CodeClassTypeImpl(
      codePackage,
      simpleName,
      types.stream()
        .map(ConvertToGenericTypeDefinable::toGenericTypeDefinable)
        .toList(),
      annotations
    );
  }

  @Override
  public String toString() {
    return identifiableName() + (genericTypes == null ? "" : genericTypes.stream()
      .map(Object::toString)
      .collect(Collectors.joining(", ", "<", ">"))
    );
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof final CodeClassType that)) {
      return false;
    }
    return Objects.equals(simpleName(), that.simpleName())
      && Objects.equals(codePackage(), that.codePackage())
      && Objects.equals(annotations(), that.annotations())
      && Objects.equals(genericTypes(), that.genericTypes());
  }

  @Override
  public int hashCode() {
    return Objects.hash(codePackage(), simpleName(), genericTypes(), annotations());
  }

  @Override
  public CodePackage codePackage() {
    return codePackage;
  }

  @Override
  public String simpleName() {
    return simpleName;
  }

  @Override
  public @Nullable List<CodeGenericTypeDefinable> genericTypes() {
    return genericTypes;
  }

  @Override
  public List<CodeAnnotation> annotations() {
    return annotations;
  }
}
