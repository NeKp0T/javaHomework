package com.example.match;

/**
 * This exception is thrown when GameCreate constructor is provided with
 * unacceptable parameters
 */
public class GameCreateException extends Throwable {
    /**
     * Constructs a new GameCreateException with provided cause
     */
    public GameCreateException(String cause) {
        super(cause);
    }
}
