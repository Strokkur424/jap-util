package net.strokkur.jap.code.classmodel;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.builder.ClassBuilder;
import net.strokkur.jap.code.classmodel.builder.RecordBuilder;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public record CodeRecord(
  CodeClassType classType,
  List<CodeGenericTypeDefinition> genericTypes,
  Set<Modifiers> modifiers,
  List<CodeAnnotation> annotations,
  List<CodeClassType> implementsTypes,

  List<CodeRecordComponent> components,
  List<CodeField> fields,
  List<CodeMethod> methods,

  @Nullable CodePrimaryConstructor primaryConstructor,
  List<CodeConstructor> additionalConstructors,

  @Nullable CodeDocumentation documentation
) implements CodeClassLike.Typed {

  public static RecordBuilder builder(String fqn) {
    return builder(CodeTypes.of(fqn));
  }

  public static RecordBuilder builder(ConvertToClassType type) {
    return new RecordBuilder(type.toClassType());
  }

  @Override
  public CodeClassType toClassType() {
    return classType();
  }

  @Override
  public <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitRecord(this);
  }
}
