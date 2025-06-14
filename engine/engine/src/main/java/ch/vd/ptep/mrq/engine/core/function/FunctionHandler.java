package ch.vd.ptep.mrq.engine.core.function;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * Injects the function handler into the function registry.
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunctionHandler {

    /**
     * The name of the function.
     */
    @AliasFor(annotation = Component.class)
    String value();
}
