package ch.vd.ptep.mrq.compiler.model.expression.arithmetic;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class SubtractOperation extends ArithmeticOperator {

    public SubtractOperation(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.SUBTRACT,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
