package com.example.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.example.qsort.QSort.partition;

/**
 * Runnable, that sorts provided list using qsort algorithm with provided execution stratege for making recursive calls.
 * Any time list element is known to take it's place in ordered list.
 * @param <T> type of list elements
 */
class QSortTask<T extends Comparable<T>> implements Runnable {
    private final List<T> list;
    private final CountDownLatch latch;
    private final RecursiveExecutionStrategy strategy;

    /**
     * Constructs new QSortTask.
     * @param list list to sort
     * @param latch
     * @param strategy
     */
    QSortTask(@NotNull List<T> list, @NotNull CountDownLatch latch, @NotNull RecursiveExecutionStrategy strategy) {
        this.list = list;
        this.latch = latch;
        this.strategy = strategy;
    }

    @Override
    public void run() {
        if (list.size() <= 1) {
            if (list.size() == 1) {
                latch.countDown();
            }
            return;
        }

        // pick middle because random is slow, and this strategy works best for already sorted lists
        T base = list.get(list.size() / 2);

        int middlePosition = partition(list, base);
        List<T> leftPart = list.subList(0, middlePosition);
        List<T> rightPart = list.subList(middlePosition + 1, list.size());
        latch.countDown();

        strategy.executeSubtasks(getSortListTask(leftPart), getSortListTask(rightPart));
    }

     private QSortTask<T> getSortListTask(List<T> listToSort) {
        return new QSortTask<>(listToSort, latch, strategy);
    }
}

