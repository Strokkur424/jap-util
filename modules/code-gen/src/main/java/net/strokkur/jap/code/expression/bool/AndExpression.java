package net.strokkur.jap.code.expression.bool;

import net.strokkur.jap.code.expression.CodeExpression;

public record AndExpression(CodeExpression left, CodeExpression right) implements CodeExpression, ScopedNot {
}
