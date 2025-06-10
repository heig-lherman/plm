package ch.vd.ptep.mrq.compiler.model.expression.arithmetic;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public abstract class ArithmeticOperator extends BinaryExpression {

    protected ArithmeticOperator(
        BinaryOperator op,
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(op, leftExpression, rightExpression);
    }
}
