package com.example.threads;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ForkJoinPool;

/**
 * A hasher that constructs a new ForkJoinPool and uses it for computing hash
 */
public class ForkJoinThreadHasher implements Hasher {

    public ForkJoinThreadHasher() {
        pool = new ForkJoinPool();
    }

    @Override
    public byte[] hash(@NotNull File toHash) {
        return pool.invoke(new HashTask(toHash));
    }

    private final ForkJoinPool pool;
}
