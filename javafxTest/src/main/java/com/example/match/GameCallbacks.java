package com.example.match;

/**
 * Callbacks used by GameInstance to notify application then
 * it should open or close cells
 */
public interface GameCallbacks {
    /**
     * This method is called then a cell should start showing it's value
     * @param x     x coordinate of opened cell
     * @param y     y coordinate of opened cell
     * @param value value to display in opened cell
     */
    void onCellOpen(int x, int y, int value);

    /**
     * This method is called then a cell should stop showing it's value
     * @param x x coordinate of closed cell
     * @param y y coordinate of closed cell
     */
    void onCellClose(int x, int y);
}
