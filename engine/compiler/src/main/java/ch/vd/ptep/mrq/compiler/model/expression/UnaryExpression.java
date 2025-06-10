package ch.vd.ptep.mrq.compiler.model.expression;

import ch.vd.ptep.mrq.compiler.model.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public abstract class UnaryExpression implements Expression {

    private final UnaryOperator operator;
    private final Expression innerExpression;

    protected UnaryExpression(
        UnaryOperator operator,
        Expression innerExpression
    ) {
        this.operator = operator;
        this.innerExpression = innerExpression;
    }

    @JsonCreator
    public static UnaryExpression create(
        @JsonProperty("op") UnaryOperator operator,
        @JsonProperty("expr") Expression expr
    ) {
        return operator.createNode(expr);
    }
}
