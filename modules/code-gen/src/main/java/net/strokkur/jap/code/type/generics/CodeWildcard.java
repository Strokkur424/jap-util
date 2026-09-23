package net.strokkur.jap.code.type.generics;

import net.strokkur.jap.code.annotations.CodeAnnotated;
import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToAnnotation;
import net.strokkur.jap.code.type.convert.ConvertToEnclosable;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CodeWildcard(
  @Nullable
  GenericEnclosure enclosure,
  List<CodeAnnotation> annotations
) implements CodeGenericTypeDefinable, CodeAnnotated {

  public static CodeWildcard ofEnclosure(GenericEnclosure enclosure) {
    return new CodeWildcard(enclosure, List.of());
  }

  public static CodeWildcard ofWildcard() {
    return new CodeWildcard(null, List.of());
  }

  public static CodeWildcard ofExtendsEnclosure(ConvertToEnclosable enclosure) {
    return ofEnclosure(GenericEnclosure.ofExtends(enclosure));
  }

  public static CodeWildcard ofSuperEnclosure(ConvertToEnclosable enclosure) {
    return ofEnclosure(GenericEnclosure.ofSuper(enclosure));
  }

  public CodeWildcard withAnnotations(List<? extends ConvertToAnnotation> annotations) {
    return new CodeWildcard(
      enclosure,
      annotations.stream()
        .map(ConvertToAnnotation::toAnnotation)
        .toList()
    );
  }

  public CodeWildcard withAnnotations(ConvertToAnnotation... annotations) {
    return withAnnotations(List.of(annotations));
  }

  @Override
  public <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitWildcard(this);
  }
}
