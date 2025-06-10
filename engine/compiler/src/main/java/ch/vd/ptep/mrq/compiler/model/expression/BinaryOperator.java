package ch.vd.ptep.mrq.compiler.model.expression;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.AddOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.DivideOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.MultiplyOperation;
import ch.vd.ptep.mrq.compiler.model.expression.arithmetic.SubtractOperation;
import ch.vd.ptep.mrq.compiler.model.expression.operator.AndExpression;
import ch.vd.ptep.mrq.compiler.model.expression.operator.OrExpression;
import ch.vd.ptep.mrq.compiler.model.expression.relational.EqualsTo;
import ch.vd.ptep.mrq.compiler.model.expression.relational.GreaterThan;
import ch.vd.ptep.mrq.compiler.model.expression.relational.GreaterThanOrEquals;
import ch.vd.ptep.mrq.compiler.model.expression.relational.LessThan;
import ch.vd.ptep.mrq.compiler.model.expression.relational.LessThanOrEquals;
import ch.vd.ptep.mrq.compiler.model.expression.relational.NotEqualsTo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BinaryOperator {
    OR("OpOr", OrExpression::new),
    AND("OpAnd", AndExpression::new),
    EQUALS("OpEq", EqualsTo::new),
    NOT_EQUALS("OpNeq", NotEqualsTo::new),
    LESS_THAN("OpLt", LessThan::new),
    LESS_THAN_OR_EQUALS("OpLte", LessThanOrEquals::new),
    GREATER_THAN("OpGt", GreaterThan::new),
    GREATER_THAN_OR_EQUALS("OpGte", GreaterThanOrEquals::new),
    ADD("OpAdd", AddOperation::new),
    SUBTRACT("OpSub", SubtractOperation::new),
    MULTIPLY("OpMul", MultiplyOperation::new),
    DIVIDE("OpDiv", DivideOperation::new);

    private final String value;
    private final BiFunction<Expression, Expression, BinaryExpression> nodeCreator;

    @JsonValue
    public String getValue() {
        return value;
    }

    public BinaryExpression createNode(
        Expression leftExpression,
        Expression rightExpression
    ) {
        return nodeCreator.apply(
            leftExpression,
            rightExpression
        );
    }

    @JsonCreator
    public static BinaryOperator fromValue(String name) {
        for (BinaryOperator operator : values()) {
            if (operator.value.equals(name)) {
                return operator;
            }
        }

        throw new IllegalArgumentException("Unknown binary operator: " + name);
    }
}
