package com.example.qsort;

import java.util.concurrent.ExecutorService;

/**
 * Execution strategy which passes one of subtasks to executor service and runs another
 */
public class OneInNewTaskStrategy implements RecursiveExecutionStrategy {

    /**
     * Constructs new instance that will use provided <code>ExecutorService</code>
     * @param executor <code>ExecutorService</code> to use for subtask
     */
    public OneInNewTaskStrategy(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void executeSubtasks(QSortTask subtask1, QSortTask subtask2) {
        executor.submit(subtask2);
        subtask1.run();
    }

    private final ExecutorService executor;
}
