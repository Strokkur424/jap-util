package net.strokkur.jap.code.classmodel;

import net.strokkur.jap.code.type.impl.CodeTypeImpl;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public interface CodePackage extends Comparable<CodePackage> {

  static CodePackage of(String packageDeclaration) {
    return CodeTypeImpl.createPackage(packageDeclaration);
  }

  static CodePackage ofEmpty() {
    return CodeTypeImpl.createPackageEmpty();
  }

  @Nullable
  @Contract(pure = true)
  String path();

  @Contract(pure = true)
  default boolean hasPath() {
    return path() != null;
  }

  @Override
  default int compareTo(CodePackage other) {
    if (Objects.equals(this.path(), other.path())) {
      return 0;
    } else if (this.path() == null) {
      return 1;
    } else if (other.path() == null) {
      return -1;
    } else {
      return this.path().compareTo(other.path());
    }
  }

  static boolean requiresImport(@Nullable CodePackage from, CodePackage other) {
    if (Objects.equals(other.path(), "java.lang")) {
      // Other path is a java.lang import, it doesn't need to be imported.
      return false;
    }

    if (other.path() == null) {
      return false;
    } else if (from == null) {
      return true;
    } else {
      return !Objects.equals(from.path(), other.path());
    }
  }
}
