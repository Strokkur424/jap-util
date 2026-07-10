package net.strokkur.jap.code.expression.bool;

import net.strokkur.jap.code.expression.CodeExpression;

public record NotExpression(CodeExpression contained) implements CodeExpression {
  /// Inverting a [NotExpression] simply yields the original expression.
  @Override
  public CodeExpression not() {
    return contained;
  }
}
