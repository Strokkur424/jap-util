package net.strokkur.jap.code.type.generics;

import net.strokkur.jap.code.type.convert.ConvertToEnclosable;
import net.strokkur.jap.code.visitor.CodeVisitable;
import net.strokkur.jap.code.visitor.CodeVisitor;
import org.jetbrains.annotations.Nullable;

/// A declaration of a generic type. This refers to the use on methods or classes
/// to add new generics. For example: `class Pair<L, R>`.
public record CodeGenericTypeDeclaration(
  String genericTypeName,
  @Nullable GenericEnclosure enclosure
) implements CodeVisitable {

  public static CodeGenericTypeDeclaration of(String name) {
    return new CodeGenericTypeDeclaration(name, null);
  }

  public static CodeGenericTypeDeclaration of(String name, @Nullable GenericEnclosure enclosure) {
    return new CodeGenericTypeDeclaration(name, enclosure);
  }

  public static CodeGenericTypeDeclaration ofExtends(String name, @Nullable ConvertToEnclosable enclosable) {
    if (enclosable == null) {
      return of(name);
    }
    return of(name, GenericEnclosure.ofExtends(enclosable));
  }

  public CodeGenericTypeDeclaration withName(String name) {
    return new CodeGenericTypeDeclaration(name, enclosure);
  }

  public CodeGenericTypeDeclaration withEnclosure(@Nullable GenericEnclosure enclosure) {
    return new CodeGenericTypeDeclaration(genericTypeName, enclosure);
  }

  public CodeGenericTypeDeclaration withExtends(@Nullable ConvertToEnclosable enclosable) {
    if (enclosable == null) {
      return withoutEnclosure();
    } else {
      return withEnclosure(GenericEnclosure.ofExtends(enclosable));
    }
  }

  public CodeGenericTypeDeclaration withoutEnclosure() {
    return new CodeGenericTypeDeclaration(genericTypeName, null);
  }

  @Override
  public <R> R accept(CodeVisitor<R> visitor) {
    return visitor.visitGenericTypeDeclaration(this);
  }
}
