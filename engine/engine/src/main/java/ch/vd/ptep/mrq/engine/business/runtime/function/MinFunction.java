package ch.vd.ptep.mrq.engine.business.runtime.function;

import ch.vd.ptep.mrq.engine.core.function.FunctionHandler;
import ch.vd.ptep.mrq.engine.core.function.support.AbstractAggregationFunction;
import java.math.BigDecimal;
import java.util.Collection;

@FunctionHandler("MIN")
public final class MinFunction extends AbstractAggregationFunction<Number> {

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
        BigDecimal min = null;
        for (Object item : items) {
            if (item instanceof Number number) {
                BigDecimal value = new BigDecimal(number.toString());
                if (min == null || value.compareTo(min) < 0) {
                    min = value;
                }
            } else {
                throw new IllegalArgumentException("All items must be instances of Number.");
            }
        }

        return min;
    }
}
