package com.example.threadpool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class ConcurrentQueueTest {
    private ConcurrentQueue<String> q;
    private ConcurrentQueue<Integer> intQ;

    @BeforeEach
    void init() {
        q = new ConcurrentQueue<>();
        intQ = new ConcurrentQueue<>();
    }

    @Test
    void singleThreadOnePushOnePopTest() throws InterruptedException {
        q.push("aaa");
        assertEquals("aaa", q.pop());
    }

    @Test
    void singleThreadMultiplePushMultiplePopTest() throws InterruptedException {
        final int pushes = 10;
        for (int i = 0; i < pushes; i++) {
            q.push("" + i);
        }
        for (int i = 0; i < pushes; i++) {
            assertEquals("" + i, q.pop());
        }
    }

    @Test
    void singleThreadMixedPushPopTest() throws InterruptedException {
        q.push("a");
        q.push("b");
        assertEquals("a", q.pop());
        q.push("c");
        assertEquals("b", q.pop());
        assertEquals("c", q.pop());
    }

    @Test
    void concurrentTest() throws InterruptedException {
        final int timesToTest = 100;
        final int threadCount = 30;
        final int timesToPush = 50;
        final var random = new Random(533);

        for (int runNumber = 0; runNumber < timesToTest; runNumber++) {

            var popped = new int[threadCount * timesToPush];

            var pushers = new ArrayList<Thread>();
            var poppers = new ArrayList<Thread>();
            for (int i = 0; i < threadCount; i++) {
                final int finalI = i;
                poppers.add(new Thread(() -> {
                    for (int j = 0; j < timesToPush; j++) {
                        popped[finalI * timesToPush + j]++;
                    }
                }));
                pushers.add(new Thread(() -> {
                    for (int j = 0; j < timesToPush; j++) {
                        intQ.push(finalI * timesToPush + j);
                    }
                }));
            }

            var allThreads = new ArrayList<Thread>();
            allThreads.addAll(poppers);
            allThreads.addAll(pushers);
            Collections.shuffle(allThreads, random);

            allThreads.forEach(Thread::start);
            for (Thread allThread : allThreads) {
                allThread.join();
            }

            Arrays.stream(popped).peek(x -> assertEquals(1, x));
        }
    }
}