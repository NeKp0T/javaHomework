package com.example.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides interface for running tasks in fixed amount of non-demon threads.
 * Tasks are taken in form of <code>Supplier<T></></code> and
 * return value is presented as {@link com.example.threadpool.LightFuture LightFuture}
 *
 * Tasks are given to workers in "first in first out" order.
 *
 * Tasks to compute {@link com.example.threadpool.LightFuture#thenApply(Function) thenApply} functions are added
 * in the pool right after original {@link com.example.threadpool.LightFuture LightFuture} is ready.
 * If original task terminated with error, then others will terminate throwing exceptions with the same exception
 * instance as cause.
 */
public class ThreadPoolImpl {

    /**
     * Constructs a new ThreadPoolImpl.
     * @param threadCount number of worker threads
     * @throws IllegalArgumentException if <code>threadCount <= 0</code>
     */
    public ThreadPoolImpl(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException();
        }
        queue = new ConcurrentQueue<>();
        workers = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            workers[i] = new Thread(() -> {
                while (!shutdown) {
                    try {
                        queue.pop().run();
                    } catch (InterruptedException e) {
                        // interruption is from the pool's shutdown method, so will end because of while condition
                    }
                }
            });
            workers[i].start();
        }
    }

    /**
     * Add provided supplier in the tasks queue.
     * @param supplier function to compute
     * @param <T> type of computation result
     * @return {@link com.example.threadpool.LightFuture LightFuture} representing computation result
     */
    public <T> LightFuture<T> execute(Supplier<T> supplier) {
        var newTask = new Task<>(supplier);
        addTask(newTask);
        return newTask;
    }

    /**
     * Interrupts all workers and prevents them from taking new tasks.
     */
    public void shutdown() {
        shutdown = true;
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

    /**
     * Adds provided task in the tasks queue.
     * @param t task to add
     */
    private void addTask(Task<?> t) {
        queue.push(t);
    }

    private final ConcurrentQueue<Task> queue;
    private final Thread[] workers;
    private volatile boolean shutdown;

    /**
     * Class that represents tasks for thread pool and serves as {@link com.example.threadpool.LightFuture LightFuture} implementation
     * @param <T> resulting type of task
     */
    private class Task<T> implements LightFuture<T>, Runnable {

        // main field to synchronize on. TODO rename
        final Object mutex;

        private Supplier<T> toCompute;

        private volatile boolean ready;
        private T result;
        private RuntimeException caughtException;

        // access only synchronized
        private final List<Task> toApply;

        /**
         * Constructs a new task to compute provided supplier
         * @param toCompute supplier to compute
         */
        private Task(Supplier<T> toCompute) {
            this.toCompute = toCompute;
            this.toApply = new ArrayList<>(); // final field
            mutex = new Object();
            ready = false; // write to volatile
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isReady() {
            return ready;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T get() throws LightExecutionException, InterruptedException {
            if (ready) {
                if (caughtException == null) {
                    return result;
                } else {
                    throw new LightExecutionException(caughtException);
                }
            }

            synchronized (mutex) {
                while (!ready) {
                    mutex.wait();
                }
                if (caughtException == null) {
                    return result;
                } else {
                    throw new LightExecutionException(caughtException);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <U> LightFuture<U> thenApply(Function<T, U> function) {
            // new task will be executed strictly after this becomes ready
            Task<U> newTask = new Task<>(() -> {
                if (caughtException != null) {
                    throw caughtException;
                }
                return function.apply(result);
            });

            if (ready) {
                addTask(newTask);
            } else {
                synchronized (toApply) {
                    if (ready) {
                        addTask(newTask);
                    } else {
                        // if this task was not ready while synchronization on toApply is acquired
                        // then toApply is not yet transferred to thread pool's queue
                        toApply.add(newTask);
                    }
                }
            }
            return newTask;
        }

        /**
         * Computes function.
         * This method is expected to be called only once.
         */
        public void run() {
            try {
                result = toCompute.get();
            } catch (RuntimeException e) {
                caughtException = e;
            }

            ready = true;
            synchronized (mutex) {
                mutex.notifyAll();
            }

            synchronized (toApply) {
                toApply.forEach(ThreadPoolImpl.this::addTask);
            }
        }
    }
}
