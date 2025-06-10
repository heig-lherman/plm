package ch.vd.ptep.mrq.engine.core.function.support;

import ch.vd.ptep.mrq.engine.core.function.RuntimeFunction;
import java.util.Arrays;

public abstract class AbstractStringFunction<T> implements RuntimeFunction<T> {

    protected abstract T apply(String... params);

    @Override
    public boolean validate(Object... params) {
        return Arrays.stream(params).allMatch(String.class::isInstance);
    }

    @Override
    public final T execute(Object... params) {
        return apply(Arrays.stream(params).map(String.class::cast).toArray(String[]::new));
    }
}
