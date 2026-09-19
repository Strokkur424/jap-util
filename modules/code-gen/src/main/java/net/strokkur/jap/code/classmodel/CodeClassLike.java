package net.strokkur.jap.code.classmodel;

import net.strokkur.jap.code.annotations.CodeAnnotated;
import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface CodeClassLike extends CodeAnnotated, ConvertToClassType, CodeVisitable {
  CodeClassType classType();

  Set<Modifiers> modifiers();

  List<CodeAnnotation> annotations();

  @Nullable CodeDocumentation documentation();

  interface Typed extends CodeClassLike {
    List<CodeGenericTypeDefinition> genericTypes();
  }
}
