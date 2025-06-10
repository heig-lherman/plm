package ch.vd.ptep.mrq.engine.business.runtime.function;

import ch.vd.ptep.mrq.engine.core.function.FunctionHandler;
import ch.vd.ptep.mrq.engine.core.function.support.AbstractStringFunction;

@FunctionHandler("LENGTH")
public final class LengthFunction extends AbstractStringFunction<Integer> {

    @Override
    protected Integer apply(String... params) {
        if (params.length == 0 || params[0] == null) {
            return 0;
        }

        return params[0].length();
    }
}
