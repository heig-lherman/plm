package ch.vd.ptep.mrq.engine.business.runtime.function;

import ch.vd.ptep.mrq.engine.core.function.FunctionHandler;
import ch.vd.ptep.mrq.engine.core.function.support.AbstractStringFunction;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@FunctionHandler("LEADINGZERO")
public final class LeadingZeroFunction extends AbstractStringFunction<Collection<Character>> {

    @Override
    protected Collection<Character> apply(String... params) {
        if (params.length == 0 || params[0] == null) {
            return List.of();
        }

        String input = params[0];
        Collection<Character> leadingZeros = new LinkedList<>();
        for (char c : input.toCharArray()) {
            if (c == '0') {
                leadingZeros.add(c);
            } else {
                break;
            }
        }

        return leadingZeros;
    }
}
