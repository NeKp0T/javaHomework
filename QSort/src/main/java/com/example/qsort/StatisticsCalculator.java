package com.example.qsort;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Runs different types of qsort and prints statistics of their running time
 */
public class StatisticsCalculator {
    private static final int TIMES_TO_RUN = 8;
    private static final int MAX_THREADS = 5;

    private static final int SIZE_LOG_FROM = 9;
    private static final int SIZE_LOG_TO = 20;

    private static final int RANDOM_SEED = 533;

    private static Duration runningTime(Runnable runnable) {
        Instant before = Instant.now();
        runnable.run();
        Instant after = Instant.now();
        return Duration.between(before, after);
    }

    private static <T extends Comparable<T>> Duration averageSortingTime(Supplier<List<T>> listSupplier, Consumer<List<T>> sorter, int timesToRun) {
        Duration summary = Duration.ZERO;
        for (int i = 0; i < timesToRun; i++) {
            List<T> list = listSupplier.get();
            summary = summary.plus(runningTime(() -> sorter.accept(list)));
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

            final List<Integer> listExample = random.ints(listSize).boxed().collect(Collectors.toList());
            Supplier<List<Integer>> listCopier = () -> new ArrayList<>(listExample);


            Duration averageTime = averageSortingTime(
                    listCopier,
                    QSort::sort,
                    TIMES_TO_RUN);
            System.out.println("Single thread average time is: " + averageTime.toMillis() + " ms");

            for (int threadCount = 1; threadCount <= MAX_THREADS; threadCount++) {
                final int threadCountCopy = threadCount;
                Duration parallelAverageTime = averageSortingTime(
                        listCopier,
                        list -> {
                            try {
                                QSort.parallelSort(list, threadCountCopy);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        TIMES_TO_RUN);

                System.out.println("For " + threadCount + " thread average time is: " + parallelAverageTime.toMillis() + " ms");
            }
            System.out.println();
        }

        // для моего 4хядерного ноута начиная с 2^12 - 2^13 сортировка в два или три потока начинает работать сравнимо
        // с однопоточной версией. А с больших листах размера >2^17 многопоточная версия даже начинает быть быстрее
        // Сортировать с числом потоков большим чем число ядер как-то медленно даже при больших листах.
        // вывод программы в output.txt в папке проекта
        return;
    }
}
