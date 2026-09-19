package net.strokkur.jap.code.classmodel.builder;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
abstract class AbstractClassLikeBuilder<R extends AbstractClassLikeBuilder<R>> {
  protected final CodeClassType type;
  protected final List<CodeGenericTypeDefinition> genericTypes = new ArrayList<>();
  protected final Set<Modifiers> modifiers = new HashSet<>();
  protected final List<CodeAnnotation> annotations = new ArrayList<>();

  protected @Nullable CodeDocumentation documentation;

  AbstractClassLikeBuilder(ConvertToClassType type) {
    this.type = type.toClassType();
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R setDocumentation(CodeDocumentation documentation) {
    this.documentation = documentation;
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addGenericTypes(CodeGenericTypeDefinition... generics) {
    this.genericTypes.addAll(List.of(generics));
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addModifiers(Modifiers... modifiers) {
    this.modifiers.addAll(Set.of(modifiers));
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addAnnotations(ConvertToClassType... annotations) {
    return addAnnotations(Arrays.stream(annotations)
      .map(CodeAnnotation::of)
      .toArray(CodeAnnotation[]::new)
    );
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addAnnotations(CodeAnnotation... annotations) {
    this.annotations.addAll(List.of(annotations));
    return (R) this;
  }
}
