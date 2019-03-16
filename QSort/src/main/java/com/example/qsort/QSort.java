package com.example.qsort;


import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides multithreaded quicksort algorithm implementation for sorting lists of comparable type
 */
public class QSort {

    /**
     * Sorts provided list using quicksort algorithm using <code>OneInNewThread</code> strategy
     * and thread pool with fixed amount of <code>threadsCount</code> threads.
     * @param list list to sort
     * @param threadsCount count of threads to use for sorting.
     * @throws IllegalArgumentException if threadCount is less then 1
     * @throws InterruptedException if thread is interrupted while waiting for sorting to complete
     * @throws NullPointerException if list is <code>null</code>
     */
    public static <T extends Comparable<T>> void parallelSort(List<T> list, int threadsCount)
            throws IllegalArgumentException, InterruptedException {
        if (threadsCount <= 0) {
            throw new IllegalArgumentException("threadCount should be more than zero");
        }
        if (list == null) {
            throw new NullPointerException("Passed list is null");
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(threadsCount);

        try {
            strategySort(list, new OneInNewTaskStrategy(threadPool));
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            throw e;
        }
        threadPool.shutdown();
    }

    /**
     * Sorts provided list using quicksort algorithm with provided recursive call strategy.
     * @param list list to sort
     * @param strategy strategy to use for recursive calls
     * @throws IllegalArgumentException if threadCount is less then 1
     * @throws InterruptedException if thread is interrupted while waiting for sorting to complete
     * @throws NullPointerException if list is <code>null</code>
     */
    public static <T extends Comparable<T>> void strategySort(List<T> list, RecursiveExecutionStrategy strategy)
            throws IllegalArgumentException, InterruptedException {
        if (list == null) {
            throw new NullPointerException("Passed list is null");
        }

        var latch = new CountDownLatch(list.size());
        new QSortTask<>(list, latch, strategy).run();
        latch.await();
    }

    /**
     * Sorts provided list using quicksort algorithm in the same thread function was called.
     * @param list list to sort
     * @throws NullPointerException if list is <code>null</code>
     */
    public static <T extends Comparable<T>> void sort(List<T> list) {
        if (list == null) {
            throw new NullPointerException("Passed list is null");
        }
        new QSortTask<>(list, new CountDownLatch(list.size()), new InSameThreadStrategy()).run();
    }

}