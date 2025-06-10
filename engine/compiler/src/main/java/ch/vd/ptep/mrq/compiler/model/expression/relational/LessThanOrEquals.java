package ch.vd.ptep.mrq.compiler.model.expression.relational;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class LessThanOrEquals extends ComparisonOperator {

    public LessThanOrEquals(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.LESS_THAN_OR_EQUALS,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
