package net.strokkur.jap.code.annotations;

import net.strokkur.jap.code.CodeGenUtil;
import net.strokkur.jap.code.type.CodeClassType;

import java.util.List;
import java.util.Objects;

public class CodeAnnotationImpl implements CodeAnnotation {
  private final CodeClassType type;
  private final List<CodeAnnotationParameter> parameters;

  protected CodeAnnotationImpl(
    CodeClassType type,
    List<CodeAnnotationParameter> parameters
  ) {
    this.type = type;
    this.parameters = List.copyOf(parameters);
  }

  @Override
  public String toString() {
    return CodeGenUtil.generateJavaStub(this);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CodeAnnotation that
      && that.type().equals(this.type)
      && that.parameters().equals(this.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, parameters);
  }

  @Override
  public CodeClassType type() {
    return type;
  }

  @Override
  public List<CodeAnnotationParameter> parameters() {
    return parameters;
  }
}
