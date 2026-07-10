package net.strokkur.jap.code.expression.bool;

import net.strokkur.jap.code.expression.CodeExpression;

public record OrExpression(CodeExpression left, CodeExpression right) implements CodeExpression, ScopedNot {
}
