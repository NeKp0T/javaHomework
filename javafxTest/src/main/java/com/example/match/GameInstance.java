package com.example.match;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static com.example.match.GameInstance.State.*;

/**
 * Represents an instance of a match two game
 */
public class GameInstance {

    private final int n;
    private final int[][] values;
    private final boolean[][] cantPick;
    private State state;
    private int openedCount = 0;
    private int moves = 0;

    private int firstPickX;
    private int firstPickY;
    private int secondPickX;
    private int secondPickY;
    private GameCallbacks callbacks;

    /**
     * Constructs a new GameInstance with provided parameters
     * @param halfN     half of field size
     * @param callbacks callbacks which will be used by GameInstance
     * @throws GameCreateException if halfN is non-positive
     */
    public GameInstance(int halfN, @NotNull GameCallbacks callbacks) throws GameCreateException {
        this.callbacks = callbacks;
        if (halfN <= 0) {
            throw new GameCreateException("Non-positive field size");
        }

        n = 2 * halfN;

        values = new int[n][n];
        cantPick = new boolean[n][n];

        var numbers = new ArrayList<Integer>();
        numbers.ensureCapacity(n * n / 2);
        for (int i = 0; i < n * n / 2; i++) {
            numbers.add(i);
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        Iterator<Integer> randomIterator = numbers.iterator();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                values[x][y] = randomIterator.next();
            }
        }

        state = READY;
    }

    /**
     * Modifies GameInstance in a way it should be modified if a user tries to open
     * a cell with provided coordinates.
     *
     * This method is safe to call anytime with any parameters.
     *
     * @param x x coordinate of a cell
     * @param y y coordinate of a cell
     */
    public void click(int x, int y) {
        if (!(coordinateInBounds(x) && coordinateInBounds(y))) {
            return;
        }

        if (canPick(x, y)) {
            cantPick[x][y] = true;
            if (state == READY) {
                firstPickX = x;
                firstPickY = y;
                state = PICKED_ONE;
            } else if (state == PICKED_ONE) {
                secondPickX = x;
                secondPickY = y;
                state = SHOWING_RESULT;
                open(firstPickX, firstPickY);
                open(x, y);

                if (openedCount == n * n) {
                    state = ENDED;
                }
            }
        }
    }

    /**
     * If game is currently in a state of showing results of clicks to a user
     * this method transforms it in a READY state
     */
    public void stopShowing() {
        if (state == SHOWING_RESULT) {
            moves++;
            if (!pickedValuesMatch()) {
                close(firstPickX,firstPickY);
                close(secondPickX,secondPickY);
            }

            state = READY;
        }
    }

    private boolean pickedValuesMatch() {
        return values[firstPickX][firstPickY] == values[secondPickX][secondPickY];
    }

    /**
     * @return a status line of a game
     */
    public String getStatusLine() {
        switch (state) {
            case READY:
                return "Pick a cell";
            case PICKED_ONE:
                return "Pick another cell";
            case SHOWING_RESULT:
                if (pickedValuesMatch()) {
                    return "A match!";
                }
                return "Remember them";
            case ENDED:
                return "You won in " + moves + " moves! Impressive!";
            default:
                throw new RuntimeException("Incomplete switch: no case for " + state);
        }
    }

    /**
     * @return a state of a game
     */
    public State getState() {
        return state;
    }

    /**
     * A getter for values in cells.
     * Throws ArrayIndexOutOfBoundsException if wrong coordinates are passed
     */
    public int getValue(int x, int y) {
        return values[x][y];
    }

    private boolean canPick(int x, int y) {
        return (state == READY || state == PICKED_ONE) && !cantPick[x][y];
    }

    private void open(int x, int y) {
        cantPick[x][y] = true;
        openedCount++;
        callbacks.onCellOpen(x, y, values[x][y]);
    }

    private void close(int x, int y) {
        cantPick[x][y] = false;
        openedCount--;
        callbacks.onCellClose(x, y);
    }

    private boolean coordinateInBounds(int coordinate) {
        return !(coordinate < 0 || coordinate >= n);
    }

    /**
     * States a game can be in.
     *
     * <code>READY</code> - ready of picking a cell
     * <code>PICKED_ONE</code> - one cell is picked, so game waits for another
     * <code>SHOWING_RESULT</code> - two cells were picked and now game shows their contents to user
     * <code>ENDED</code> - all cells were opened and a game has ended
     */
    public enum State {
        READY, PICKED_ONE, SHOWING_RESULT, ENDED
    }
}
