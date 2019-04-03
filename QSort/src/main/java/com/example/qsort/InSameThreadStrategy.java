package com.example.qsort;

/**
 * Strategy that runs both subtasks
 */
public class InSameThreadStrategy implements RecursiveExecutionStrategy {

    /**
     *  {@inheritDoc}
     */
    @Override
    public void executeSubtasks(QSortTask subtask1, QSortTask subtask2) {
        subtask1.run();
        subtask2.run();
    }
}
