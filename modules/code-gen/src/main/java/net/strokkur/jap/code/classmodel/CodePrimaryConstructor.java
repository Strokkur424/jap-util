package net.strokkur.jap.code.classmodel;

import net.strokkur.jap.code.annotations.CodeAnnotated;
import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.builder.PrimaryConstructorBuilder;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToPrimaryConstructor;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public record CodePrimaryConstructor(
  CodeClassType type,
  List<CodeGenericTypeDefinition> generics,

  List<CodeAnnotation> annotations,
  Set<Modifiers> modifiers,
  List<CodeClassType> throwsExceptions,

  @Nullable CodeDocumentation documentation,

  CodeBlock code
) implements CodeAnnotated, ConvertToPrimaryConstructor, CodeVisitable {

  public static PrimaryConstructorBuilder builder(ConvertToClassType type) {
    return new PrimaryConstructorBuilder(type.toClassType());
  }

  @Override
  public CodePrimaryConstructor toPrimaryConstructor() {
    return this;
  }

  @Override
  public <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitPrimaryConstructor(this);
  }
}
