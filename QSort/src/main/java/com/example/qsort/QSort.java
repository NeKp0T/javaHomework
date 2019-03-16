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


    /**
     * Partitions provided list by comparing with base element.
     * After method execution all elements prior to returned index are guaranteed to be
     * less or equal, then provided base element and all elements from returned index to the end of list
     * to be greater or equal then base.
     * Additionally, If provided list contains at list one value equal to base, returned index
     * will be occupied by one of them.
     *
     * Requires list to contain at least two items.
     *
     * @return index, that every element in list before it is lower or equals to base,
     *          and every element after it is greater or equals to base.
     */
    static <T extends Comparable<T>> int partition(@NotNull List<T> list, @NotNull T base) {
        ListIterator<T> forward = list.listIterator();
        ListIterator<T> backward = list.listIterator(list.size());
        T leftItem = forward.next();
        T rightItem = backward.previous();

        while (true) {
            while (forward.hasNext() && leftItem.compareTo(base) < 0) {
                leftItem = forward.next();
            }
            while (backward.hasPrevious() && rightItem.compareTo(base) > 0) {
                rightItem = backward.previous();
            }

            if (forward.previousIndex() >= backward.nextIndex()) {
                break;
            }

            forward.set(rightItem);
            backward.set(leftItem);

            if (leftItem.compareTo(base) != 0) {
                leftItem = rightItem;
                rightItem = backward.previous();
            } else {
                rightItem = leftItem;
                leftItem = forward.next();
            }
        }
        return forward.previousIndex();
    }

}