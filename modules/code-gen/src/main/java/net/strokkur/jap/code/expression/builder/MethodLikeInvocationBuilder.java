package net.strokkur.jap.code.expression.builder;

import net.strokkur.jap.code.convert.ConvertToExpression;
import net.strokkur.jap.code.convert.ConvertToFieldMethodSource;
import net.strokkur.jap.code.expression.source.FieldMethodSource;
import net.strokkur.jap.code.util.StyleConfig;

public interface MethodLikeInvocationBuilder <S extends MethodLikeInvocationBuilder<S>> extends ConvertToExpression, FieldMethodSource {
  S setSource(ConvertToFieldMethodSource source);

  S setStyle(StyleConfig style);

  S addParameters(ConvertToExpression... parameters);
}
