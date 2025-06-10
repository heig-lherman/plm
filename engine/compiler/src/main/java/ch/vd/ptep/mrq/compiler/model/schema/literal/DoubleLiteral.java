package ch.vd.ptep.mrq.compiler.model.schema.literal;

import ch.vd.ptep.mrq.compiler.model.ExpressionVisitor;
import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;

public record DoubleLiteral(
    BigDecimal value
) implements NumericLiteral<BigDecimal, DoubleLiteral> {

    @Override
    public boolean hasFloatingPoint() {
        return true;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(@NotNull DoubleLiteral o) {
        return value.compareTo(o.value());
    }
}
