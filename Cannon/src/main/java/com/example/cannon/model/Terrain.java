package com.example.cannon.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

public class Terrain {
    public final int width;
    public final int height;

    private final Block[][] terrain;

    /**
     * Radius to calculate collision vector
     */
    private static final int COLLISION_RADIUS_CHECK = 3;

    public static Terrain constructSinusoidalTerrain(int width, int height) {
        Terrain construct = new Terrain(width, height);
        // hardcoded terrain initialization
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y < (height / 7) * (2 + Math.sin(x * 10.0 / width))) {
                    construct.terrain[x][y] = new Block(Block.GROUND);
                } else {
                    construct.terrain[x][y] = new Block(Block.EMPTY);
                }
            }
            construct.terrain[width / 2][y].updateType(Block.OTHER);
        }
        return construct;
    }

    private Terrain(int width, int height) {
        this.width = width;
        this.height = height;

        terrain = new Block[width][height];
    }

    public boolean detectCollisionCircle(Vector2 position, double radius) {
        int rUp = (int) Math.ceil(radius) + 1;
        int xRound = (int) Math.round(position.x);
        int yRound = (int) Math.round(position.y);

        for (int x = xRound - rUp; x <= xRound + rUp; x++) {
            for (int y = yRound - rUp; y <= yRound + rUp; y++) {
                if (inBounds(x, y) && position.difference(new Vector2(x, y)).lengthSq() <= radius * radius) {
                    if (!terrain[x][y].isFree()) {
                        return true;
                    }
                }
            }
        }

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

    // TODO find more places to use
    private Stream<Block> inCircle(Vector2 middle, double r) {
        int x = (int) Math.round(middle.x);
        int y = (int) Math.round(middle.y);
        int rInt = (int) Math.round(r) + 1;
        var blocks = new ArrayList<Block>();
        for (int dx = -rInt; dx <= rInt; dx++) {
            for (int dy = -rInt; dy <= rInt; dy++) {
                int xNew = x + dx;
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

    public void setCanvas(Canvas terrainCanvas, Function<Integer, ? extends Color> colorPicker) {
        PixelWriter pixelWriter = terrainCanvas.getGraphicsContext2D().getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int xCanvas = x;
                final int yCanvas = height - y - 1;
                IntConsumer lambdaToSet = t -> pixelWriter.setColor(xCanvas, yCanvas, colorPicker.apply(t));
                terrain[x][y].onUpdateSet(lambdaToSet);
                lambdaToSet.accept(terrain[x][y].getType());
            }
        }
    }

    public void destroyInRadius(Vector2 position, double radius) {
        inCircle(position, radius).forEach(block -> block.updateType(Block.EMPTY));
    }
}
