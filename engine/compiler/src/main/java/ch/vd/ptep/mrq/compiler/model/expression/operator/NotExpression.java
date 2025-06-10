package ch.vd.ptep.mrq.compiler.model.expression.operator;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.UnaryOperator;

public class NotExpression extends UnaryLogicExpression {

    public NotExpression(
        Expression innerExpression
    ) {
        super(
            UnaryOperator.NOT,
            innerExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
