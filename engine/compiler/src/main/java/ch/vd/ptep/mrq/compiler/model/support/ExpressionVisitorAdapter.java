package ch.vd.ptep.mrq.compiler.model.support;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.BinaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.UnaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.AddOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.DivideOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.MultiplyOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.SubtractOperation;
import ch.vd.ptep.mrq.compiler.model.expression.functional.FunctionCall;
import ch.vd.ptep.mrq.compiler.model.expression.operator.AndExpression;
import ch.vd.ptep.mrq.compiler.model.expression.operator.NotExpression;
import ch.vd.ptep.mrq.compiler.model.expression.operator.OrExpression;
import ch.vd.ptep.mrq.compiler.model.expression.relational.EqualsTo;
import ch.vd.ptep.mrq.compiler.model.expression.relational.GreaterThan;
import ch.vd.ptep.mrq.compiler.model.expression.relational.GreaterThanOrEquals;
import ch.vd.ptep.mrq.compiler.model.expression.relational.LessThan;
import ch.vd.ptep.mrq.compiler.model.expression.relational.LessThanOrEquals;
import ch.vd.ptep.mrq.compiler.model.expression.relational.NotEqualsTo;
import ch.vd.ptep.mrq.compiler.model.schema.Identifier;
import ch.vd.ptep.mrq.compiler.model.schema.literal.DoubleLiteral;
import ch.vd.ptep.mrq.compiler.model.schema.literal.IntegerLiteral;
import ch.vd.ptep.mrq.compiler.model.schema.literal.NullLiteral;
import ch.vd.ptep.mrq.compiler.model.schema.literal.StringLiteral;
import java.util.Arrays;
import java.util.Collection;

/**
 * An abstract adapter class for visiting expressions. This class provides default implementations for all methods in
 * the {@link ExpressionVisitor} interface, allowing subclasses to override only the methods they are interested in.
 *
 * @param <T> the return type of the visitor methods
 */
public abstract class ExpressionVisitorAdapter<T> implements ExpressionVisitor<T> {

    @Override
    public T visit(AndExpression andExpression) {
        return visitBinaryExpression(andExpression);
    }

    @Override
    public T visit(OrExpression orExpression) {
        return visitBinaryExpression(orExpression);
    }

    @Override
    public T visit(NotExpression notExpression) {
        return visitUnaryExpression(notExpression);
    }

    @Override
    public T visit(EqualsTo equalsTo) {
        return visitBinaryExpression(equalsTo);
    }

    @Override
    public T visit(NotEqualsTo equalsTo) {
        return visitBinaryExpression(equalsTo);
    }

    @Override
    public T visit(LessThan lessThan) {
        return visitBinaryExpression(lessThan);
    }

    @Override
    public T visit(LessThanOrEquals lessThanOrEquals) {
        return visitBinaryExpression(lessThanOrEquals);
    }

    @Override
    public T visit(GreaterThan greaterThan) {
        return visitBinaryExpression(greaterThan);
    }

    @Override
    public T visit(GreaterThanOrEquals greaterThanOrEquals) {
        return visitBinaryExpression(greaterThanOrEquals);
    }

    @Override
    public T visit(AddOperation addition) {
        return visitBinaryExpression(addition);
    }

    @Override
    public T visit(SubtractOperation subtraction) {
        return visitBinaryExpression(subtraction);
    }

    @Override
    public T visit(MultiplyOperation multiplication) {
        return visitBinaryExpression(multiplication);
    }

    @Override
    public T visit(DivideOperation division) {
        return visitBinaryExpression(division);
    }

    @Override
    public T visit(FunctionCall functionCall) {
        return visitExpressions(
            functionCall,
            Arrays.asList(functionCall.args())
        );
    }

    @Override
    public T visit(Identifier identifier) {
        return null;
    }

    @Override
    public T visit(StringLiteral stringLiteral) {
        return null;
    }

    @Override
    public T visit(IntegerLiteral integerLiteral) {
        return null;
    }

    @Override
    public T visit(DoubleLiteral doubleLiteral) {
        return null;
    }

    @Override
    public T visit(NullLiteral nullLiteral) {
        return null;
    }

    protected T visitUnaryExpression(UnaryExpression unaryExpression) {
        return visitExpressions(
            unaryExpression,
            unaryExpression.getInnerExpression()
        );
    }

    protected T visitBinaryExpression(BinaryExpression binaryExpression) {
        return visitExpressions(
            binaryExpression,
            binaryExpression.getLeftExpression(),
            binaryExpression.getRightExpression()
        );
    }

    protected T visitExpressions(Expression expression, Expression... subExpressions) {
        return visitExpressions(
            expression,
            Arrays.asList(subExpressions)
        );
    }

    protected T visitExpressions(Expression expression, Collection<Expression> expressions) {
        for (var e : expressions) {
            if (e != null) {
                e.accept(this);
            }
        }

        return null;
    }
}
