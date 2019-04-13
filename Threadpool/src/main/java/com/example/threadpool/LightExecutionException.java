package com.example.threadpool;

/**
 * An exception representing error while computing a {@link com.example.threadpool.LightFuture LightFuture} result.
 *
 * Original exception is stored as suppressed.
 */
public class LightExecutionException extends Exception {
    LightExecutionException(RuntimeException e) {
        super(e);
    }
}
