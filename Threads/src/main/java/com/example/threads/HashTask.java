package com.example.threads;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.RecursiveTask;

/**
 * A task to compute hash in a ForkJoinPool
 */
class HashTask extends RecursiveTask<byte[]> {

    public HashTask(File fileToHash) {
        this.fileToHash = fileToHash;
    }

    @Override
    protected byte[] compute() {
        AbstractHasherImpl innerHasher = new AbstractHasherImpl() {
            @Override
            protected byte[] hashDirectory(File directory) {
                MessageDigest digestMessage = constructMD5Message();
                var taskList = new ArrayList<HashTask>();

                for (File f : Objects.requireNonNull(directory.listFiles())) {
                    var task = new HashTask(f);
                    task.fork();
                    taskList.add(task);
                }

                digestMessage.digest(directoryNameToBytes(directory));

                for (HashTask task : taskList) {
                    digestMessage.update(task.join());
                }
                return digestMessage.digest();
            }
        };
        return innerHasher.hash(fileToHash);
    }

    private final File fileToHash;
}