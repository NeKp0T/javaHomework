package com.example.qsort;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StaticticsCalculator {
    private static final int TIMES_TO_RUN = 8;
    private static final int MAX_THREADS = 6;

    private static final int SIZE_LOG_FROM = 6;
    private static final int SIZE_LOG_TO = 19;

    private static final int RANDOM_SEED = 533;

    private static <T extends Comparable<T>> Duration sortingTime(List<T> list, int threadCount) throws InterruptedException {
        Instant before = Instant.now();
        QSort.parallelSort(list, threadCount);
        Instant after = Instant.now();
        return Duration.between(before, after);
    }

    private static <T extends Comparable<T>> Duration averageSortingTime(List<T> list, int threadCount, int timesToRun) throws InterruptedException {
        Duration summary = Duration.ZERO;
        for (int i = 0; i < timesToRun; i++) {
            summary = summary.plus(sortingTime(list, threadCount));
        }
        return summary.dividedBy(timesToRun);
    }

    public static void main(String[] args) {
        System.out.println("Running every test " + TIMES_TO_RUN + " times to calculate average");
        System.out.println("Random seed is " + RANDOM_SEED);
        System.out.println();

        Random random = new Random(RANDOM_SEED);

        for (int listSizeLog = SIZE_LOG_FROM; listSizeLog <= SIZE_LOG_TO; listSizeLog++) {
            int listSize = 1 << listSizeLog;
            System.out.println("List size: " + listSize + " = 2^" + listSizeLog);

            List<Integer> listExample = new ArrayList<>(random.ints(listSize).boxed().collect(Collectors.toList()));

            try {
                for (int threadCount = 1; threadCount <= MAX_THREADS; threadCount++) {
                    var listToSort = new ArrayList<Integer>(listExample);
                    Duration averageTime = averageSortingTime(listToSort, threadCount, TIMES_TO_RUN);
                    System.out.println("For " + threadCount + " thread average time is: " + averageTime.toMillis() + " ms");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            System.out.println();
        }

        // для моего 4хядерного ноута начиная с 16384 - 32768 сортировка в два потока начинает выигрывать.
        // Сортировать с большим числом потоков становется медленнее.
        // Но все равно при больших размерах получается быстрее чем с одним потоком
    }
}
