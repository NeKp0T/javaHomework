package com.example.threads;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A class that implements most part of hashing algorithm, requiring only
 * <code>hashDirectory(dir)</code> to be implemented.
 */
abstract class AbstractHasherImpl implements Hasher {
    @Override
    public byte[] hash(@NotNull File toHash) {
        if (toHash.isDirectory()) {
            return hashDirectory(toHash);
        }
        if (toHash.isFile()) {
            return hashFile(toHash);
        }
        return new byte[0]; // weird file
    }

    protected abstract byte[] hashDirectory(File toHash);

    private byte[] hashFile(File file) {
        MessageDigest digestMessage = constructMD5Message();

        try (var fileStream = new FileInputStream(file)) {
            var digestStream = new DigestInputStream(fileStream, digestMessage);
            digestStream.readAllBytes();
            return digestStream.getMessageDigest().digest();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    byte[] directoryNameToBytes(File directory) {
        return directory.getName().getBytes();
    }

    static MessageDigest constructMD5Message() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
