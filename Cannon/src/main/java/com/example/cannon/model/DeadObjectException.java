package com.example.cannon.model;

/**
 * This exception occurs then trying to access world from a RoundObject that is unregistered from
 * it's world.
 */
public class DeadObjectException extends RuntimeException {
    /**
     * Constructs a new DeadObjectException exception
     * @param what cause of exception
     */
    public DeadObjectException(String what) {
        super(what);
    }

    public DeadObjectException() {
        super();
    }
}
