package ch.vd.ptep.mrq.compiler.model.expression.arithmetic;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public class AddOperation extends ArithmeticOperator {

    public AddOperation(
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(
            BinaryOperator.ADD,
            leftExpression,
            rightExpression
        );
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
