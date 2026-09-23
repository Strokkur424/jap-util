package net.strokkur.jap.code.type.impl;

import net.strokkur.jap.code.classmodel.CodePackage;

import java.util.Objects;

record CodePackageImpl(String packageString) implements CodePackage {

  @Override
  public String path() {
    return packageString;
  }

  @Override
  public boolean hasPath() {
    return true;
  }

  @Override
  public String toString() {
    return path();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof final CodePackage that)) {
      return false;
    }
    return Objects.equals(path(), that.path());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(path());
  }
}
