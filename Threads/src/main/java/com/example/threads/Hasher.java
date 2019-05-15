package com.example.threads;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * An interface for hashing files and directories.
 *
 * Hash function f is computed by following rules:
 * f(file) = MD5(file's contents)
 * f(dir) = MD5(dir's name as bytes + f(file1) + ...)
 *
 * Files reading of which is forbidden files reading of which is forbidden and other filesystem
 * entities, which are not <code>.isFile()</code> or <code>isDirectory()</code> and are skipped.
 */
public interface Hasher {
    /**
     * @param toHash file or directory to hash
     * @return a hash of provided entity
     */
    default byte[] hash(@NotNull File toHash) {
        return new byte[0];
    }
}
