package ch.vd.ptep.mrq.compiler.model.expression;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.expression.operator.NotExpression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UnaryOperator {
    NOT("OpNot", NotExpression::new);

    private final String value;
    private final Function<Expression, UnaryExpression> nodeCreator;

    @JsonValue
    public String getValue() {
        return value;
    }

    public UnaryExpression createNode(
        Expression innerExpression
    ) {
        return nodeCreator.apply(innerExpression);
    }

    @JsonCreator
    public static UnaryOperator fromValue(String name) {
        for (UnaryOperator operator : values()) {
            if (operator.value.equals(name)) {
                return operator;
            }
        }

        throw new IllegalArgumentException("Unknown binary operator: " + name);
    }
}
