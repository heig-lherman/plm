package ch.vd.ptep.mrq.compiler.model.expression.relational;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class GreaterThan extends ComparisonOperator {

    public GreaterThan(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.GREATER_THAN,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
