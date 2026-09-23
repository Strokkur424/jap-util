package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodePackage;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.convert.ConvertToGenericType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.generic.CodeGenericType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

record CodeClassTypeImpl(
  CodePackage codePackage,
  String simpleName,
  @Nullable List<CodeGenericType> genericTypes,
  List<CodeAnnotation> annotations
) implements CodeClassType {

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
  public CodeClassType typed(List<? extends ConvertToGenericType> types) {
    return new CodeClassTypeImpl(
      codePackage,
      simpleName,
      types.stream()
        .map(ConvertToGenericType::toGenericType)
        .toList(),
      annotations
    );
  }

  @Override
  public String toString() {
    return identifiableName() + (genericTypes == null ? "" : genericTypes.stream()
      .map(CodeGenericType::toString)
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
}
