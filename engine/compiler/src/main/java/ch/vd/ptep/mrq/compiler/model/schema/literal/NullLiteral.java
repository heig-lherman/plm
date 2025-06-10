package ch.vd.ptep.mrq.compiler.model.schema.literal;

import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;

public record NullLiteral(
) implements Literal {

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
