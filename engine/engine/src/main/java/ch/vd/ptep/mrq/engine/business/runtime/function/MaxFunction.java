package ch.vd.ptep.mrq.engine.business.runtime.function;

import ch.vd.ptep.mrq.engine.core.function.FunctionHandler;
import ch.vd.ptep.mrq.engine.core.function.support.AbstractAggregationFunction;
import java.math.BigDecimal;
import java.util.Collection;

@FunctionHandler("MAX")
public final class MaxFunction extends AbstractAggregationFunction<Number> {

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
        BigDecimal max = null;
        for (Object item : items) {
            if (item instanceof Number number) {
                BigDecimal value = new BigDecimal(number.toString());
                if (max == null || value.compareTo(max) > 0) {
                    max = value;
                }
            } else {
                throw new IllegalArgumentException("All items must be instances of Number.");
            }
        }

        return max;
    }
}
