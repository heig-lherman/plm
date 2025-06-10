package ch.vd.ptep.mrq.model.rule;

import org.jspecify.annotations.NonNull;

public interface Rule<T> {

    /**
     * @return the rule's identifier
     */
    String name();

    /**
     * @return the rule's description
     */
    String description();

    /**
     * @return the rule's metadata
     */
    RuleMetadata metadata();

    /**
     * @return the rule's root class
     */
    Class<T> getRootClass();

    /**
     * Checks the rule and returns the result.
     *
     * @param object  the object to validate
     * @param context the rule context
     * @return the result of the validation
     */
    RuleResult validate(@NonNull T object, @NonNull RuleContext context);
}
