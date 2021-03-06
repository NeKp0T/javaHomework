package com.example.qsort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class QSortTest {
    private ArrayList<Integer> list;
    private Random randomSource;

    @BeforeEach
    void init() {
        list = new ArrayList<>();
        randomSource = new Random(533);
    }

    private void fillList(int size) {
        list = new ArrayList<>();
        list.addAll(randomSource.ints(size).boxed().collect(Collectors.toList()));
    }

    private <T> int callPartition(List<T> listToPartition, T base) throws InvocationTargetException, IllegalAccessException {
        // now it is package-private, but in case it will become private this code will be useful
        Method[] methods = QSortTask.class.getDeclaredMethods();
        Method partitionMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("partition")) {
                partitionMethod = method;
            }
        }

        partitionMethod.setAccessible(true);
        return (int) partitionMethod.invoke(null, listToPartition, base);
    }

    private <T extends Comparable<T>> void checkPartitionCorrectness(List<T> listToTest, T base, int pos) {
        listToTest.subList(0, pos).forEach(element -> assertTrue(element.compareTo(base) <= 0));
        listToTest.subList(pos, listToTest.size()).forEach(element -> assertTrue(element.compareTo(base) >= 0));
        assertEquals(0, base.compareTo(listToTest.get(pos)));
    }

    private <T extends Comparable<T>> void checkPartition(List<T> listToTest, T base) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<T> copyOfList = new ArrayList<>(listToTest);
        checkPartitionCorrectness(listToTest, base, callPartition(listToTest, base));
        copyOfList.sort(Comparator.naturalOrder());
        listToTest.sort(Comparator.naturalOrder());
        assertEquals(copyOfList, listToTest);
    }

    @Test
    void somePartitionTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        list.addAll(List.of(1, 5, 3, 4, 2));
        checkPartition(list, 3);
    }

    @Test
    void otherPartitionTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        list.addAll(List.of(2, 1, 3));
        checkPartition(list, 1);
    }

    @Test
    void sortedPartitionTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        list = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9));
        checkPartition(list, 5);
        list = new ArrayList<>(List.of(9,8,7,6,5,4,3,2,1));
        checkPartition(list, 5);
    }

    @Test
    void firstAndLastElementsPartitionTests() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        list = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9));
        checkPartition(list, 1);
        list = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9));
        checkPartition(list, 9);
        list = new ArrayList<>(List.of(5,2,4,5,6));
        checkPartition(list, 2);
        list = new ArrayList<>(List.of(5,2,4,5,6));
        checkPartition(list, 6);
    }

    @Test
    void repeatingBaseElementTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        list = new ArrayList<>(List.of(1,2,2,1,2,3,2,3,2));
        checkPartition(list, 2);
    }

    @Test
    void randomPartitionTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < 100; i++) {
            fillList(1000);
            checkPartition(list, list.get(1));
        }
    }

    private <T extends Comparable<T>> void checkSortCorrectness(List<T> listToTest, int threadsCount)
            throws InterruptedException {
        List<T> listCopy = new ArrayList<>(listToTest);
        QSort.parallelSort(list, threadsCount);
        listCopy.sort(Comparator.naturalOrder());
        assertEquals(listCopy, listToTest);
    }

    private <T extends Comparable<T>> void checkSortCorrectness(List<T> listToTest) {
        List<T> listCopy = new ArrayList<>(listToTest);
        QSort.sort(list);
        listCopy.sort(Comparator.naturalOrder());
        assertEquals(listCopy, listToTest);
    }

    private <T extends Comparable<T>> void checkSortCorrectness(List<T> listToTest, RecursiveExecutionStrategy strategy)
            throws InterruptedException {
        List<T> listCopy = new ArrayList<>(listToTest);
        QSort.strategySort(list, strategy);
        listCopy.sort(Comparator.naturalOrder());
        assertEquals(listCopy, listToTest);
    }

    @Test
    void oneThreadParallelQSortTest() throws InterruptedException {
        list = new ArrayList<>(List.of(5,1,4,3,2));
        checkSortCorrectness(list, 1);
    }

    @Test
    void notParallelQSortTest() {
        list = new ArrayList<>(List.of(5,1,4,3,2));
        checkSortCorrectness(list);
    }

    @Test
    void InSameThreadSortTest() throws InterruptedException {
        list = new ArrayList<>(List.of(5,1,4,3,2));
        checkSortCorrectness(list, new InSameThreadStrategy());
    }

    @Test
    void OneInSameThread4Threads() throws InterruptedException {
        var executor = Executors.newFixedThreadPool(4);
        list = new ArrayList<>(List.of(5,1,4,3,2));
        checkSortCorrectness(list, new OneInNewTaskStrategy(executor));
    }

    @Test
    void BothInSameThread4Threads() throws InterruptedException {
        var executor = Executors.newFixedThreadPool(4);
        list = new ArrayList<>(List.of(5,1,4,3,2));
        checkSortCorrectness(list, new BothInNewTaskStrategy(executor));
    }

    @Test
    void fourThreadQSortTest() throws InterruptedException {
        list = new ArrayList<>(List.of(5,1,4,3,2));
        checkSortCorrectness(list, 4);
    }

    @Test
    void negativeThreadQSortTest() {
        fillList(10);
        assertThrows(IllegalArgumentException.class, () -> checkSortCorrectness(list, -1));
    }

    @Test
    void nullAsListTest() {
        assertThrows(NullPointerException.class, () -> QSort.parallelSort(null, 2));
        assertThrows(NullPointerException.class, () -> QSort.sort(null));
    }

    @Test
    void randomParallelSortTest() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            fillList(100);
            checkSortCorrectness(list, 4);
        }
    }

    @Test
    void randomNotParallelSortTest() {
        for (int i = 0; i < 100; i++) {
            fillList(100);
            checkSortCorrectness(list);
        }
    }
}
