package ch.vd.ptep.mrq.engine.core.function;

/**
 * Interface for runtime StandardQL functions that can be executed with parameters.
 *
 * @param <T> the type of the result returned by the function
 */
public interface RuntimeFunction<T> {

    /**
     * Typechecks the parameters to ensure they are valid for the function.
     *
     * @param params the parameters to typecheck
     * @return true if the parameters are valid, false otherwise
     */
    boolean validate(Object... params);

    /**
     * Executes the function with the given parameters.
     *
     * @param params the parameters to execute the function with
     * @return the result of the function execution
     */
    T execute(Object... params);
}
