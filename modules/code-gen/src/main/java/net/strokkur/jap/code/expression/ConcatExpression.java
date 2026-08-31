package net.strokkur.jap.code.expression;

import net.strokkur.jap.code.expression.source.FieldMethodSource;

import java.util.List;

public record ConcatExpression(
  List<CodeExpression> expressions
) implements CodeExpression, RequiresBracketsOnAccess, FieldMethodSource {
}
