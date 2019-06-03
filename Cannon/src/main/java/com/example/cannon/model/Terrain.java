package com.example.cannon.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A class to store and manipulate terrain of a world.
 *
 * Terrain consists of a matrix of blocks which are initialized in constructor
 * and then never replaced. Blocks can be changed using through their methods though.
 */
public final class Terrain {
    public final int width;
    public final int height;

    private final @NotNull Block[][] terrain;

//    /**
//     * Radius to calculate collision vector
//     */
    // currently unused
    private static final int COLLISION_RADIUS_CHECK = 3;

    /**
     * Constructs and returns a sine-wave-like terrain with specified parameters
     */
    public static @NotNull Terrain constructSinusoidalTerrain(int width, int height) {
        Terrain construct = new Terrain(width, height);
        // hardcoded terrain initialization
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y < (height / 7) * (2 + Math.sin(x * 10.0 / width))) {
                    construct.terrain[x][y] = new Block(Block.BlockType.GROUND);
                } else {
                    if (y * 3 < height) {
                        construct.terrain[x][y] = new Block(Block.BlockType.BACKGROUND);
                    } else {
                        construct.terrain[x][y] = new Block(Block.BlockType.EMPTY);
                    }
                }
            }
            construct.terrain[width / 2][y].updateType(Block.BlockType.WALL);
        }
        return construct;
    }

    private Terrain(int width, int height) {
        this.width = width;
        this.height = height;

        terrain = new Block[width][height];
    }

    /**
     * Checks if a circle with specified center and radius intersects with
     * any of non-passable blocks or a boundbox.
     * @return <code>true</code> if there is a collision
     */
    public boolean detectCollisionCircle(@NotNull Vector2 center, double radius) {
        int rUp = (int) Math.ceil(radius) + 1;
        int xRound = (int) Math.round(center.x);
        int yRound = (int) Math.round(center.y);

        for (int x = xRound - rUp; x <= xRound + rUp; x++) {
            for (int y = yRound - rUp; y <= yRound + rUp; y++) {
                if (inBounds(x, y) && center.difference(new Vector2(x, y)).lengthSq() <= radius * radius) {
                    if (!terrain[x][y].isFree()) {
                        return true;
                    }
                }
            }
        }

        return detectBoundboxCollisionCircle(center, radius);
    }

    /**
     * Binds all blocks to provided canvas so that this canvas always represent
     * actual blocks state
     * Canvas is expected to match dimensions of a terrain.
     */
    public void setCanvas(@NotNull Canvas terrainCanvas) {
        PixelWriter pixelWriter = terrainCanvas.getGraphicsContext2D().getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int xCanvas = x;
                final int yCanvas = height - y - 1;
                Consumer<Block.BlockType> lambdaToSet = t -> pixelWriter.setColor(xCanvas, yCanvas, t.color);
                terrain[x][y].onUpdateSet(lambdaToSet);
                lambdaToSet.accept(terrain[x][y].getType());
            }
        }
    }

    /**
     * Makes all blocks in provided circle empty
     */
    public void destroyInRadius(@NotNull Vector2 center, double radius) {
        inCircle(center, radius).forEach(block -> block.updateType(Block.BlockType.EMPTY));
    }

    private boolean detectBoundboxCollisionCircle(@NotNull Vector2 position, double radius) {
        return position.x <= radius || position.y <= radius
                || (width - position.x) <= radius || (height - position.y) <= radius;
    }

    // currently unused
    @SuppressWarnings("unused")
    private @NotNull Vector2 collisionVector(@NotNull Vector2 position) {
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

    /**
     * Checks if provided coordinates contain a passable block
     */
    private boolean free(int x, int y) {
        return inBounds(x, y) && terrain[x][y].isFree();
    }

    private @NotNull Stream<Block> inCircle(@NotNull Vector2 middle, double r) {
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
}
