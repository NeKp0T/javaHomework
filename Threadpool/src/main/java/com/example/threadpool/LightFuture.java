package com.example.threadpool;

import java.util.function.Function;

/**
 * Represents result of asynchronous computation.
 * Methods are provided to check if computation is completed, to wait and get it's result
 * and to compose it with other function.
 * @param <T> the result type
 */
public interface LightFuture<T> {

    /**
     * Checks if computation is completed. Note that in case computation ended with an error
     * this method would return <code>true</code> anyway.
     * @return if computation is completed.
     */
    boolean isReady();

    /**
     * Waits for computation to complete and returns it's result.
     *
     * If exception occurs while computing it a new <code>LightExeutionException</code> will be thrown
     * with original exception as cause.
     *
     * Thread calling <code>get</code> will not be used for computation.
     * @return result of computation
     * @throws LightExecutionException if exception occurs while calculating result.
     * @throws InterruptedException if the current thread is interrupted while waiting for computation to finish.
     */
    T get() throws LightExecutionException, InterruptedException;

    /**
     * Creates a new <code>LightFuture</code> representing application of provided function
     * to the result of current <code>LightFuture</code>.
     *
     *
     * @param function function to apply to the result of <code>LightFuture</code>
     * @param <U>
     * @return
     */
    <U> LightFuture<U> thenApply(Function<T, U> function);
}
