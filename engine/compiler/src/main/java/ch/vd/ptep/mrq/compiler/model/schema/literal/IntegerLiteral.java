package ch.vd.ptep.mrq.compiler.model.schema.literal;

import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import org.jetbrains.annotations.NotNull;

public record IntegerLiteral(
    Long value
) implements NumericLiteral<Long, IntegerLiteral> {

    @Override
    public boolean hasFloatingPoint() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(@NotNull IntegerLiteral o) {
        return value.compareTo(o.value());
    }
}
