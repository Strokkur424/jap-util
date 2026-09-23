package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.classmodel.CodePackage;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class CodePackageEmptyImpl implements CodePackage {
  static CodePackage INSTANCE = new CodePackageEmptyImpl();

  private CodePackageEmptyImpl() {
    // noop
  }

  @Override
  public @Nullable String path() {
    return null;
  }

  @Override
  public boolean hasPath() {
    return false;
  }

  @Override
  public String toString() {
    return "[no package]";
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CodePackage that && !that.hasPath();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(false);
  }
}
