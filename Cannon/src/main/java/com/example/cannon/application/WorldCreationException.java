package com.example.cannon.application;

/**
 * An exception thrown if a world could not be created by GameInstance.
 * Possible cause - too many players.
 */
public class WorldCreationException extends Exception {
    public WorldCreationException(String s) {
        super(s);
    }
}
