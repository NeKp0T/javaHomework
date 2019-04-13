package com.example.threadpool;

/**
 * A concurrent queue that allows simultaneous pushing and popping if it has at least 3 elements.
 * Does not allow null to be stored
 * @param <T> type of queue elements
 */
class ConcurrentQueue<T> {

    /**
     * Constructs a new ConcurrentQueue
     */
    public ConcurrentQueue() {
        pushMutex = new Object();
        popMutex = new Object();
    }

    /**
     * Added provided element to queue
     * @param element element to add
     */
    void push(T element) {
        QueueElement newNode = new QueueElement(element);
        synchronized (pushMutex) {
            if (last != null) {
                last.next = newNode;
                last = newNode;
            } else {
                // takes other mutex only if queue is empty
                synchronized (popMutex) {
                    first = newNode;
                    last = newNode;
                }
            }
        }
        popMutex.notify();
    }

    /**
     * Pops an element from queue. If queue is empty then waits for a new element.
     * @return a popped element
     */
    T pop() throws InterruptedException {
        synchronized (popMutex) {
            while (first == null) {
                popMutex.wait();
            }

            T popped = first.value;
            if (first.next != null) {
                first = first.next;
            } else {
                // takes other mutex only if queue is not empty (yet)
                synchronized (pushMutex) {
                    if (first.next != null) {
                        first = first.next;
                    } else {
                        first = null;
                        last = null;
                    }
                }
            }

            return popped;
        }
    }

    // emptiness changes only when both mutexes are acquired
    private final Object pushMutex;
    private final Object popMutex;

    private QueueElement first;
    private QueueElement last;

    /**
     * A simple structure representing one node of a queue.
     */
    private class QueueElement {
        QueueElement next;
        private T value;

        QueueElement(T value) {
            this.value = value;
        }
    }
}
