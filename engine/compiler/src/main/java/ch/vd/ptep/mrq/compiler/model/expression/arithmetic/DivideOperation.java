package ch.vd.ptep.mrq.compiler.model.expression.arithmetic;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class DivideOperation extends ArithmeticOperator {

    public DivideOperation(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.DIVIDE,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
