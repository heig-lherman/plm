package ch.vd.ptep.mrq.compiler.model.expression.functional;

import ch.vd.ptep.mrq.compiler.model.Expression;
import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;

public record FunctionCall(
    String name,
    FunctionArgument... args
) implements Expression, FunctionArgument {

    public FunctionCall(String name) {
        this(name, new FunctionArgument[0]);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
