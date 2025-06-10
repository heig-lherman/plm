package ch.vd.ptep.mrq.engine.business.runtime.function;

import ch.vd.ptep.mrq.engine.core.function.FunctionHandler;
import ch.vd.ptep.mrq.engine.core.function.support.AbstractAggregationFunction;
import java.util.Collection;

@FunctionHandler("COUNT")
public final class CountFunction extends AbstractAggregationFunction<Integer> {

    @Override
    protected Integer apply(Collection<Object> items) {
        return items.size();
    }
}
