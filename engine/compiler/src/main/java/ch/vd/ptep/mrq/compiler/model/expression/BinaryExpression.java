package ch.vd.ptep.mrq.compiler.model.expression;

import ch.vd.ptep.mrq.compiler.model.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class BinaryExpression implements Expression {

    private final BinaryOperator operator;
    private final Expression leftExpression;
    private final Expression rightExpression;

    protected BinaryExpression(
        BinaryOperator operator,
        Expression leftExpression,
        Expression rightExpression
    ) {
        this.operator = operator;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @JsonCreator
    public static BinaryExpression create(
        @JsonProperty("op") BinaryOperator operator,
        @JsonProperty("left") Expression left,
        @JsonProperty("right") Expression right
    ) {
        return operator.createNode(left, right);
    }
}
