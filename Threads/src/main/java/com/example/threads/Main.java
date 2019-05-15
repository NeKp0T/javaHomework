package com.example.threads;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * A console application for comparing running times of single-thread and fork-join
 * implementations of hasher.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Print a path to a file or a directory");
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
            String path = input.readLine();
            File f = new File(path.strip());
            if (!f.exists()) {
                System.out.println("Sorry, that file does not exist");
                return;
            }

            TestResults resultsSingleThread = test(new SingleThreadHasher(), f);
            System.out.println("Single thread running time: " + resultsSingleThread.time.toMillis() + "ms");

            TestResults resultsForkJoin = test(new ForkJoinThreadHasher(), f);
            System.out.println("Fork join thread running time: " + resultsForkJoin.time.toMillis() + "ms");

            if (Arrays.equals(resultsSingleThread.result, resultsForkJoin.result)) {
                System.out.println("Results are equal: ");
                System.out.println(Arrays.toString(resultsSingleThread.result));
            } else {
                System.out.println("Results are different: ");
                System.out.println(Arrays.toString(resultsSingleThread.result));
                System.out.println(Arrays.toString(resultsForkJoin.result));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TestResults test(Hasher hasher, File fileToHash) {
        Instant before = Instant.now();
        byte[] result = hasher.hash(fileToHash);
        Instant after = Instant.now();
        return new TestResults(Duration.between(before, after), result);
    }

    private static class TestResults {
        public final Duration time;
        public final byte[] result;

        private TestResults(Duration time, byte[] result) {
            this.time = time;
            this.result = result;
        }
    }
}
