package com.example.cannon.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public class RoundObject {
    /**
     * Minimal distance to the ground on which object considers itself falling
     */
    private static final double FALLING_PRECISION = 0.2;
    /**
     * By how mush <code>FALLING_PRECISION</code> should an object fall each step
     */
    private static final int FALL_PER_STEP = 6;

    protected final int radius;
    protected World world;
    protected boolean affectedByGravity = true;

    protected Vector2 position;

    private boolean inWorld;

    RoundObject(int radius, Vector2 position, World world) {
        this.position = position;
        this.radius = radius;
        this.world = world;

        world.addObject(this);
        inWorld = true;
    }

    boolean detectTerrainCollision() {
        return world.getTerrain().detectCollisionCircle(position, radius);
    }

    /**
     * Simulates how physics affects this object in one step.
     * This method is called until all objects in the world are not finished with their physics.
     * @return if this object is still affected by physics
     */
    public boolean physicsStep() {
        return fallOneStep() != 0;
    }

    protected final void unregister() {
        world.remove(this);
        inWorld = false;
        world = null;
    }

    public final boolean isInWorld() {
        return inWorld;
    }

    public void render(GraphicsContext graphics) {
        Vector2 canvasPosition = getCanvasPosition();
        graphics.strokeOval(canvasPosition.x - radius, canvasPosition.y - radius, radius * 2, radius * 2);
    }

    protected Vector2 getCanvasPosition() {
        return new Vector2(position.x, world.getTerrain().height - 1 - position.y);
    }

    /**
     * All RoundObjects are compared by reference.
     * It is needed so <code>world</code> would remove them correctly
     */
    @Override
    public final boolean equals(Object other) {
        return this == other;
    }

    public Vector2 getPositionCopy() {
        return new Vector2(position);
    }

    /**
     * Simulates fall on one <code>FALLING_PRECISION</code>.
     * @return how much object fell in this step. If object didn't fall returned value if precisely zero.
     */
    protected double fallOneStep() {
        if (!affectedByGravity || detectTerrainCollision()) {
            return 0;
        }
        double sumDistance = 0;
        for (int i = 0; i < FALL_PER_STEP; i++) {
            sumDistance += tryFallOneQuant();
        }
        return sumDistance;
    }

    /**
     * Tries to fall by one <code>FALLING_PRECISION</code>
     * @return fall distance
     */
    private double tryFallOneQuant() {
        double oldY = position.y;
        position.y -= FALLING_PRECISION;
        if (detectTerrainCollision()) {
            position.y = oldY;
        }
        return oldY - position.y;
    }
}
