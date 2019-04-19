package com.example.threadpool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolImplTest {
    private final static int timesToRunEveryTest = 50;

    private ThreadPoolImpl singleThreadPool;
    private ThreadPoolImpl fourThreadPool;

    private static Stream<Arguments> threadPoolsSource() {
        int[] threadCounts = new int[]{1, 4, 10};
        return Stream.generate(new Supplier<Arguments>() {
            @Override
            public Arguments get() {
                return Arguments.of(new ThreadPoolImpl(threadCounts[iteration++ % threadCounts.length]));
            }

            int iteration = 0;

        }).limit(threadCounts.length * timesToRunEveryTest);
    }

    @BeforeEach
    void init() {
        singleThreadPool = new ThreadPoolImpl(1);
        fourThreadPool = new ThreadPoolImpl(4);
    }

    @ParameterizedTest
    @MethodSource("threadPoolsSource")
    void threadPoolWorks(ThreadPoolImpl threadPool) throws LightExecutionException, InterruptedException {
        final var array = new int[5];
        var futures = new ArrayList<LightFuture<Integer>>();
        for (int i = 0; i < array.length; i++) {
            final int index = i;
            futures.add(threadPool.execute(() -> array[index] = index));
        }
        for (int i = 0; i < array.length; i++) {
            futures.get(i).get();
            assertEquals(i, array[i]);
        }
    }

    @RepeatedTest(100)
    void singleThreadTaskOrder() throws LightExecutionException, InterruptedException {
        final int count = 5;
        var n = new int[1];
        var futures = new ArrayList<LightFuture<Integer>>();
        for (int i = 0; i < count; i++) {
            futures.add(singleThreadPool.execute(() -> n[0]++));
        }
        for (int i = count - 1; i >= 0; i--) {
            assertEquals(futures.get(i).get(), i);
        }
    }

    @ParameterizedTest
    @MethodSource("threadPoolsSource")
    void thenApplyAfterExecutionWorks(ThreadPoolImpl threadPool) throws LightExecutionException, InterruptedException {
        LightFuture<Integer> task = threadPool.execute(() -> 0);
        task.get();
        LightFuture<Integer> thenTask = task.thenApply(n -> n + 1);
        assertEquals(1, thenTask.get());
    }

    @ParameterizedTest
    @MethodSource("threadPoolsSource")
    void thenApplyBeforeExecutionWorks(ThreadPoolImpl threadPool) throws LightExecutionException, InterruptedException {
        var waitForTask = new Object();
        LightFuture<Integer> thenTask;
        synchronized (waitForTask) {
            LightFuture<Integer> task = threadPool.execute(() -> {
                synchronized (waitForTask) {
                    return 0;
                }
            });
            thenTask = task.thenApply(n -> n + 1);
        }
        assertEquals(1, thenTask.get());
    }

    @RepeatedTest(timesToRunEveryTest)
    void thenApplyDoesNotBlock() throws LightExecutionException, InterruptedException {
        var waitForTask = new Object();
        synchronized (waitForTask) {
            LightFuture<Integer> task = fourThreadPool.execute(() -> {
                synchronized (waitForTask) {
                    return 0;
                }
            });
            for (int i = 0; i < 100; i++) {
                task.thenApply(n -> n + 1);
            }
            fourThreadPool.execute(() -> 1).get();
        }
    }

    @RepeatedTest(timesToRunEveryTest)
    void shutdownInterrupts() throws InterruptedException, LightExecutionException {
        var waitForTask = new Object();
        var freezeTask = new Object();
        LightFuture<Integer> future;

        synchronized (waitForTask) {
            future = fourThreadPool.execute(() -> {
                synchronized (freezeTask) {
                    synchronized (waitForTask) { // acquires after waitForTask.wait() in main thread
                        waitForTask.notifyAll();
                    }
                    //noinspection CatchMayIgnoreException
                    try {
                        freezeTask.wait();
                    } catch (InterruptedException e) {
                    }
                }
                return 0;
            });
            waitForTask.wait(); // wakes up after waitForTask synchronization in the pool
        }
        synchronized (freezeTask) { // acquires after freezeTask.wait() in the pool
        }
        fourThreadPool.shutdown();
        assertEquals(0, future.get());
    }

    @RepeatedTest(timesToRunEveryTest)
    void poolHasFourThreads() throws LightExecutionException, InterruptedException {
//      threadCount = 4;
        var mutexes = new Object[3];
        for (int i = 0; i < mutexes.length; i++) {
            mutexes[i] = new Object();
        }

        synchronized (mutexes[0]) {
            synchronized (mutexes[1]) {
                synchronized (mutexes[2]) {
                    for (Object mutex : mutexes) {
                        fourThreadPool.execute(() -> {
                            synchronized (mutex) {
                            }
                            return mutex;
                        });
                    }
                    assertEquals(0, fourThreadPool.execute(() -> 0).get());
                }
            }
        }
    }

    @RepeatedTest(timesToRunEveryTest)
    void isReadyTest() throws LightExecutionException, InterruptedException {
        var waitForTask = new Object();
        LightFuture<Integer> task;
        synchronized (waitForTask) {
            task = fourThreadPool.execute(() -> {
                synchronized (waitForTask) {
                    return 0;
                }
            });
            assertFalse(fourThreadPool.execute(() -> 1).isReady());
        }
        task.get();
        assertTrue(task.isReady());
    }

    @ParameterizedTest
    @MethodSource("threadPoolsSource")
    void futureThrows(ThreadPoolImpl threadPool) throws InterruptedException {
        final var toThrow = new RuntimeException("bob");
        LightFuture<Integer> future = threadPool.execute(() -> {
                    throw toThrow;
                }
        );
        try {
            future.get();
        } catch (LightExecutionException e) {
            assertEquals(toThrow, e.getCause());
        }
    }
}
