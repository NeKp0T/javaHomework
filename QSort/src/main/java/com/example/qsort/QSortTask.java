package com.example.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

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

    /**
     * Runs qsort on contained list
     */
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
    private static <T extends Comparable<T>> int partition(@NotNull List<T> list, @NotNull T base) {
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

