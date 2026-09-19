package net.strokkur.jap.code.classmodel.builder;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.convert.ConvertToStatement;
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
abstract class AbstractConstructorLikeBuilder<R extends AbstractConstructorLikeBuilder<R>> {
  protected final CodeClassType type;
  protected @Nullable CodeDocumentation documentation = null;
  protected CodeBlock codeBlock = CodeBlock.of();

  protected final List<CodeGenericTypeDefinition> generics = new ArrayList<>();
  protected final List<CodeAnnotation> annotations = new ArrayList<>();
  protected final Set<Modifiers> modifiers = new HashSet<>();
  protected final List<CodeClassType> throwsExceptions = new ArrayList<>();

  AbstractConstructorLikeBuilder(CodeClassType type) {
    this.type = type;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R setDocumentation(CodeDocumentation documentation) {
    this.documentation = documentation;
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R setCodeBlock(ConvertToStatement... statements) {
    this.codeBlock = CodeBlock.of(statements);
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addAnnotations(CodeAnnotation... annotations) {
    this.annotations.addAll(List.of(annotations));
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addGenerics(CodeGenericTypeDefinition... generics) {
    this.generics.addAll(List.of(generics));
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addModifiers(Modifiers... modifiers) {
    this.modifiers.addAll(List.of(modifiers));
    return (R) this;
  }

  @Contract(value = "_ -> this", mutates = "this")
  public R addThrowsExceptions(ConvertToClassType... throwsExceptions) {
    this.throwsExceptions.addAll(Arrays.stream(throwsExceptions)
      .map(ConvertToClassType::toClassType)
      .toList()
    );
    return (R) this;
  }
}
