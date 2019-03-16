package com.example.qsort;

import java.util.concurrent.ExecutorService;

/**
 * Execution strategy which passes both tasks to executor service
 */
class BothInNewTaskStrategy implements RecursiveExecutionStrategy {

    /**
     * Constructs new instance that will use provided <code>ExecutorService</code>
     * @param executor <code>ExecutorService</code> to use for subtasks
     */
    public BothInNewTaskStrategy(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void executeSubtasks(QSortTask subtask1, QSortTask subtask2) {
        executor.submit(subtask1);
        executor.submit(subtask2);
    }

    private final ExecutorService executor;
}
