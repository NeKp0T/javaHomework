package com.example.qsort;

/**
 * Interface for executing qsort recursive calls
 */
interface RecursiveExecutionStrategy {
    /**
     * Method that executes provided subtasks in a way defined by implementation
     * @param subtask1
     * @param subtask2
     */
    void executeSubtasks(QSortTask subtask1, QSortTask subtask2);
}
