package net.strokkur.jap.code.classmodel;

import net.strokkur.jap.code.annotations.CodeAnnotated;
import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.CodeVisitor;

import java.util.List;

public record CodeRecordComponent(
  CodeType type,
  String name,
  List<CodeAnnotation> annotations
) implements CodeAnnotated, CodeVisitable {

  public static CodeRecordComponent of(ConvertToType type, String name, CodeAnnotation... annotations) {
    return new CodeRecordComponent(type.toType(), name, List.of(annotations));
  }

  @Override
  public <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitRecordComponent(this);
  }
}
