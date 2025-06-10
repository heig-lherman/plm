package ch.vd.ptep.mrq.compiler.model.expression.operator;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class OrExpression extends BinaryLogicExpression {

    public OrExpression(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.OR,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
