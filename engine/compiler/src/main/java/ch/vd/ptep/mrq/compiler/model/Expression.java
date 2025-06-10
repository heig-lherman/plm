package ch.vd.ptep.mrq.compiler.model;

import ch.vd.ptep.mrq.compiler.model.expression.BinaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.UnaryExpression;
import ch.vd.ptep.mrq.compiler.model.expression.functional.FunctionCall;
import ch.vd.ptep.mrq.compiler.model.schema.Identifier;
import ch.vd.ptep.mrq.compiler.model.schema.literal.Literal;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * Expressions are valued elements of the language, which can be evaluated or transformed.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BinaryExpression.class, name = "BinaryExpr"),
    @JsonSubTypes.Type(value = UnaryExpression.class, name = "UnaryExpr"),
    @JsonSubTypes.Type(value = FunctionCall.class, name = "FunctionCall"),
    @JsonSubTypes.Type(value = Identifier.class, name = "IdentExpr"),
    @JsonSubTypes.Type(value = Literal.class, name = "ValueExpr"),
})
public interface Expression {

    /**
     * An exception accepts its visitors, to form the double-dispatch part of the visitor pattern. Allowing for
     * implementation of inference and evaluation systems as well as code generation.
     *
     * @param visitor the visitor to accept
     */
    <T> T accept(ExpressionVisitor<T> visitor);

    /**
     * Unwraps the current expression to the expected type.
     *
     * @param expected the expected class
     * @param <T>      the expected class type
     * @return the unwrapped expression
     */
    default <T> T as(Class<T> expected) {
        if (!expected.isInstance(this)) {
            throw new IllegalStateException("Expected %s but got %s".formatted(expected, getClass()));
        }

        return expected.cast(this);
    }
}
