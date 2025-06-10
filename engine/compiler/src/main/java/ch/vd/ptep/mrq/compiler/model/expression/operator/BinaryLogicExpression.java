package ch.vd.ptep.mrq.compiler.model.expression.operator;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public abstract class BinaryLogicExpression extends BinaryExpression implements LogicExpression {

    protected BinaryLogicExpression(
        BinaryOperator operator,
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(operator, leftExpression, rightExpression);
    }
}
