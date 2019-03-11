package com.example.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.example.qsort.QSort.partition;

class QSortTask<T extends Comparable<T>> implements Runnable {
    final List<T> list;
    final CountDownLatch latch;
    final ExecutorService threadPool;

    QSortTask(@NotNull List<T> list, @NotNull CountDownLatch latch, @NotNull ExecutorService threadPool) {
        this.list = list;
        this.latch = latch;
        this.threadPool = threadPool;
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

        threadPool.submit(getSortListTask(leftPart));
        threadPool.submit(getSortListTask(rightPart));
    }

     private Runnable getSortListTask(List<T> listToSort) {
        return new QSortTask<>(listToSort, latch, threadPool);
    }
}

