package net.strokkur.jap.code.expression.bool;

import net.strokkur.jap.code.convert.ConvertToExpression;

public final class BooleanExpressions {

  public static NotExpression not(ConvertToExpression expr) {
    return new NotExpression(expr.toExpression());
  }

  public static AndExpression and(ConvertToExpression left, ConvertToExpression right) {
    return new AndExpression(left.toExpression(), right.toExpression());
  }

  public static OrExpression or(ConvertToExpression left, ConvertToExpression right) {
    return new OrExpression(left.toExpression(), right.toExpression());
  }

  private BooleanExpressions() throws IllegalAccessError {
    throw new IllegalAccessError("You cannot instantiate this class.");
  }
}
