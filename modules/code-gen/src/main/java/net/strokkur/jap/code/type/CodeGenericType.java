package net.strokkur.jap.code.type;

import net.strokkur.jap.code.annotations.CodeAnnotated;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.generics.CodeEnclosable;
import net.strokkur.jap.code.type.impl.CodeGenericTypeImpl;

import java.util.List;

public sealed interface CodeGenericType
  extends CodeType, CodeAnnotated, CodeEnclosable
  permits CodeGenericTypeImpl {

  String name();

  @Override
  default String simpleName() {
    return name();
  }

  @Override
  default String fullyQualifiedName() {
    return name();
  }

  @Override
  CodeGenericType withAnnotations(List<? extends ConvertToAnnotation> annotations);

  @Override
  default CodeGenericType withAnnotations(ConvertToAnnotation... annotations) {
    return withAnnotations(List.of(annotations));
  }
}
