package ch.vd.ptep.mrq.engine.business.runtime.function;

import ch.vd.ptep.mrq.engine.core.function.FunctionHandler;
import ch.vd.ptep.mrq.engine.core.function.support.AbstractAggregationFunction;
import java.math.BigDecimal;
import java.util.Collection;

@FunctionHandler("SUM")
public final class SumFunction extends AbstractAggregationFunction<Number> {

    @Override
    protected boolean check(Collection<Object> items) {
        if (items.isEmpty()) {
            return false;
        }

        for (Object item : items) {
            if (!(item instanceof Number)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected Number apply(Collection<Object> items) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Object item : items) {
            if (item instanceof Number number) {
                // Convert the number to BigDecimal for accurate summation
                BigDecimal value = new BigDecimal(number.toString());
                sum = sum.add(value);
            } else {
                throw new IllegalArgumentException("All items must be instances of Number.");
            }
        }

        return sum;
    }
}
