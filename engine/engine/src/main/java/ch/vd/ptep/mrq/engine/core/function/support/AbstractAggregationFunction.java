package ch.vd.ptep.mrq.engine.core.function.support;

import ch.vd.ptep.mrq.engine.core.function.RuntimeFunction;
import java.util.Collection;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractAggregationFunction<T> implements RuntimeFunction<T> {

    protected boolean check(Collection<Object> items) {
        return true;
    }

    protected abstract T apply(Collection<Object> items);

    @Override
    public final boolean validate(Object... params) {
        if (params.length < 1 || !(params[0] instanceof Collection coll)) {
            return false;
        }

        return check(coll);
    }

    @Override
    public final T execute(Object... params) {
        if (params.length < 1 || !(params[0] instanceof Collection coll)) {
            throw new IllegalArgumentException("Expected a collection as the first parameter.");
        }

        return (T) apply(coll);
    }
}
