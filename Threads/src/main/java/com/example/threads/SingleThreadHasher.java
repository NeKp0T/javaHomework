package com.example.threads;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * A hasher that works in a single thread
 */
public class SingleThreadHasher extends AbstractHasherImpl {

    protected byte[] hashDirectory(@NotNull File directory) {
        MessageDigest digestMessage = constructMD5Message();

        digestMessage.digest(directoryNameToBytes(directory));

        for (File f : Objects.requireNonNull(directory.listFiles())) {
            digestMessage.update(hash(f));
        }
        return digestMessage.digest();
    }
}
