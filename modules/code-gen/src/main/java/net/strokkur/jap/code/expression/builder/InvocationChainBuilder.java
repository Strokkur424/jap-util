package net.strokkur.jap.code.expression.builder;

import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToStatement;
import net.strokkur.jap.code.expression.CodeExpression;
import net.strokkur.jap.code.expression.Invocation;
import net.strokkur.jap.code.expression.source.FieldMethodSource;
import net.strokkur.jap.code.util.StyleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class InvocationChainBuilder implements ConvertToExpression, ConvertToStatement {
  private final FieldMethodSource source;
  private final List<Function<FieldMethodSource, Invocation>> chainedMethods = new ArrayList<>();

  public InvocationChainBuilder(FieldMethodSource source) {
    this.source = source;
  }

  public InvocationChainBuilder chainField(String fieldName) {
    chainedMethods.add((source) -> source.chainField(fieldName));
    return this;
  }

  public InvocationChainBuilder chainMethod(String name, ConvertToExpression... parameters) {
    chainedMethods.add((source) -> source.chainMethod(name, parameters).toMethodInvocation());
    return this;
  }

  public InvocationChainBuilder chainMethod(String name, StyleConfig styleConfig, ConvertToExpression... parameters) {
    chainedMethods.add((source) -> source.chainMethod(name, parameters).setStyle(styleConfig).toMethodInvocation());
    return this;
  }

  @Override
  public CodeExpression toExpression() {
    if (chainedMethods.isEmpty()) {
      throw new IllegalStateException("Tried to build MethodInvocationChainBuilder without any methods chained.");
    }

    Invocation curr = chainedMethods.getFirst().apply(source);
    for (int i = 1; i < chainedMethods.size(); i++) {
      curr = chainedMethods.get(i).apply(curr);
    }
    return curr.toExpression();
  }
}
