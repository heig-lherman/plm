package ch.vd.ptep.mrq.compiler.model.expression.operator;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class AndExpression extends BinaryLogicExpression {

    public AndExpression(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.AND,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
