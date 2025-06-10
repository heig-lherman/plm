package ch.vd.ptep.mrq.compiler.model.expression.operator;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.UnaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.UnaryOperator;

public abstract class UnaryLogicExpression extends UnaryExpression implements LogicExpression {

    protected UnaryLogicExpression(
        UnaryOperator operator,
        Expression innerExpression
    ) {
        super(operator, innerExpression);
    }
}
