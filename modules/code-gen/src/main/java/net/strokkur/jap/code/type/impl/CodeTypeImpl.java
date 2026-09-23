package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.annotations.CodeAnnotation;
import net.strokkur.jap.code.classmodel.CodePackage;
import net.strokkur.jap.code.type.CodeArrayType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePrimitiveType;
import net.strokkur.jap.code.type.CodeType;
import net.strokkur.jap.code.type.CodeGenericType;
import net.strokkur.jap.code.type.generics.CodeGenericTypeDefinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Exposes the standard implementations of the CodeType interfaces.
@ApiStatus.Internal
public final class CodeTypeImpl {

  public static CodeClassType createClass(
    CodePackage codePackage,
    String simpleName,
    @Nullable List<CodeGenericTypeDefinable> genericTypes,
    List<CodeAnnotation> annotations
  ) {
    return new CodeClassTypeImpl(
      codePackage,
      simpleName,
      genericTypes,
      annotations
    );
  }

  public static CodePackage createPackage(String packageDeclaration) {
    return new CodePackageImpl(packageDeclaration);
  }

  public static CodePackage createPackageEmpty() {
    return CodePackageEmptyImpl.INSTANCE;
  }

  public static CodePrimitiveType createPrimitive(String name, String boxedName, List<CodeAnnotation> annotations) {
    return new CodePrimitiveTypeImpl(
      name,
      boxedName,
      annotations
    );
  }

  public static CodeArrayType createArray(CodeType inner, List<CodeAnnotation> annotations) {
    return new CodeArrayTypeImpl(
      inner,
      annotations
    );
  }

  public static CodeGenericType createGeneric(String name, List<CodeAnnotation> annotations) {
    return new CodeGenericTypeImpl(
      name,
      annotations
    );
  }

  private CodeTypeImpl() throws IllegalAccessError {
    throw new IllegalAccessError("You may not create a new instance of this class.");
  }
}
