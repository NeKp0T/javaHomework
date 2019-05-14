package com.example.cannon.model;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Terrain {
    private final int width;
    private final int height;

    private final Block[][] terrain;

    /**
     * Radius to calculate collision vector
     */
    private static final int COLLISION_RADIUS_CHECK = 3;

    public Terrain(int width, int height) {
        this.width = width;
        this.height = height;

        terrain = new Block[height][width];
        // hardcoded terrain initialization
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y < (height / 7) * (2 + Math.sin(x * 10.0 / width))) {
                    terrain[x][y] = new Block(1);
                } else {
                    terrain[x][y] = new Block(0);
                }
            }
        }
    }

    public boolean detectCollisionCircle(Vector2 position, double radius) {
        boolean ok = true;

        return detectBoundboxCollisionCircle(position, radius);
    }

    private boolean detectBoundboxCollisionCircle(Vector2 position, double radius) {
        return position.x <= radius || position.y <= radius
                || (width - position.x) <= radius || (height - position.y) <= radius;
    }

    private Vector2 collisionVector(Vector2 position) {
        int x = (int) Math.round(position.x);
        int y = (int) Math.round(position.y);

        var result = new Vector2();
        for (int dx = -COLLISION_RADIUS_CHECK; dx <= COLLISION_RADIUS_CHECK; dx++) {
            for (int dy = -COLLISION_RADIUS_CHECK; dy <= COLLISION_RADIUS_CHECK; dy++) {
                if (dx * dx + dy * dy <= COLLISION_RADIUS_CHECK) {
                    if (!free(x + dx, y + dy)) {
                        result.x -= dx;
                        result.y -= dy;
                    }
                }
            }
        }
        return result;
    }

    private boolean free(int x, int y) {
        return inBounds(x, y) && terrain[x][y].isFree();
    }

    private Stream<Block> inCircle(Vector2 middle, double r) {
        int x = (int) Math.round(middle.x);
        int y = (int) Math.round(middle.y);
        int rInt = (int) Math.round(r) + 1;
        var blocks = new ArrayList<Block>();
        for (int dx = -rInt; dx <= rInt; dx++) {
            for (int dy = -rInt; dy <= rInt; dy++) {
                int xNew = x + dy;
                int yNew = y + dy;
                if (new Vector2(xNew, yNew).difference(middle).lengthSq() <= r * r
                    && inBounds(xNew, yNew)) {
                    blocks.add(terrain[xNew][yNew]);
                }
            }
        }
        return blocks.stream();
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
