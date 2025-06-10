package ch.vd.ptep.mrq.compiler.model.schema;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import ch.vd.ptep.mrq.compiler.model.expression.functional.FunctionArgument;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Identifier(
    String name
) implements Expression, FunctionArgument {

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @JsonCreator(mode = Mode.PROPERTIES)
    public static Identifier fromExpression(
        @JsonProperty("ident") String name
    ) {
        return new Identifier(name);
    }
}
