package ch.vd.ptep.mrq.compiler.model.expression.relational;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryOperator;

public abstract class ComparisonOperator extends BinaryExpression {

    protected ComparisonOperator(
        BinaryOperator op,
        Expression leftExpression,
        Expression rightExpression
    ) {
        super(op, leftExpression, rightExpression);
    }
}
