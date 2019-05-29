package com.example.match;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameInstanceTest {

    private GameInstance game;
    private GameCallbacks callbacks;

    @BeforeEach
    void initDefault() throws GameCreateException {
        callbacks = new GameCallbacks() {
            @Override
            public void onCellOpen(int x, int y, int value) {}

            @Override
            public void onCellClose(int x, int y) {}
        };
        game = new GameInstance(4, callbacks);
    }

    @Test
    void throwsOnNegative() {
        assertThrowsOnCreation(-2);
    }

    @Test
    void throwsOnZero() {
        assertThrowsOnCreation(0);
    }

    @Test
    void doesntThrowOnCreation() {
        assertDoesNotThrow(() -> new GameInstance(2, callbacks));
    }

    @Test
    void opensCells() throws GameCreateException {
        boolean[] openedCell = new boolean[2];
        int x1 = 0;
        int y1 = 0;
        int x2 = 1;
        int y2 = 1;
        game = new GameInstance(4, new GameCallbacks() {
            @Override
            public void onCellOpen(int x, int y, int value) {
                assertEquals(game.getValue(x, y), value);
                if (x == x1 && y == y1) {
                    openedCell[0] = true;
                    return;
                }
                if (x == x2 && y == y2) {
                    openedCell[1] = true;
                    return;
                }
                fail();
            }

            @Override
            public void onCellClose(int x, int y) {

            }
        });
        game.click(x1, y1);
        game.click(x2, y2);
        assertTrue(openedCell[0]);
        assertTrue(openedCell[1]);
    }

    private void assertThrowsOnCreation(int halfN) {
        assertThrows(GameCreateException.class, () -> new GameInstance(halfN, callbacks));
    }
}
