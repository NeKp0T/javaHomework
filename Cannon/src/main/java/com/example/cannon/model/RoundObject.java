package com.example.cannon.model;

import javafx.scene.canvas.GraphicsContext;

// TODO notnull everywhere

/**
 * Basic class for all objects in the world.
 * Any <code>RoundObject</code> is bound to a specific world from creation
 * and unbound by calling <code>unregister()</code> method to remove it from
 * the world.
 */
public class RoundObject {
    /**
     * Minimal distance to the ground on which object considers itself falling
     */
    private static final double FALLING_PRECISION = 0.2;
    /**
     * By how mush <code>FALLING_PRECISION</code> should an object fall each step
     */
    private static final int FALL_PER_STEP = 6;

    /**
     * A radius of round object.
     * It is used for collision calculations.
     */
    protected final int radius;

    private World world;

    /**
     * @return A world this unit is bound to or <code>null</code> if it is unregistered
     */
    public final World getWorld() {
        return world;
    }

    /**
     * If this object should be affected by gravity or not.
     * Subclasses that redefine physics might use it for their own purposes freely.
     */
    protected boolean affectedByGravity = true;

    /**
     * Position of object in the world
     */
    protected Vector2 position;

    /**
     * Constructs a new <code>RoundObject</code> in a specified place of provided world
     */
    protected RoundObject(int radius, Vector2 position, World world) {
        this.position = position;
        this.radius = radius;
        this.world = world;

        world.addObject(this);
    }

    /**
     * Calculates distance from unit's edge to specified position.
     * Returns negative values if position is inside the unit
     * @param position position to check distance to
     * @return distance from unit's edge to specified position
     */
    public double getDistance(Vector2 position) {
        return this.position.difference(position).length() - radius;
    }

    /**
     * @return if object collides with terrain of the world
     */
    protected boolean detectTerrainCollision() {
        return world.getTerrain().detectCollisionCircle(position, radius);
    }

    /**
     * Simulates how physics affects this object in one step.
     * This method is called until all objects in the world are not finished with their physics,
     * so it should stop returning true if called consequently many times.
     *
     * Subclasses are free to redefine this method.
     *
     * @return if this object is still affected by physics
     */
    protected boolean physicsStep() {
        return fallOneStep() != 0;
    }

    /**
     * Removes an object from it's world.
     * After this all world-related methods will result in a null pointer exception.
     */
    protected final void unregister() {
        world.remove(this);
        world = null;
    }

    /**
     * @return if an object is still bound to it's world.
     */
    public final boolean isInWorld() {
        return world != null;
    }

    /**
     * @return radius of a round object
     */
    public final double getRadius() {
        return radius;
    }

    /**
     * Renders an object on a context that is expected to match dimensions of
     * the world an object is bound to.
     *
     * Subclasses are free to redefine this method, while keeping in mind that
     * in context coordinates y is reversed.
     *
     * @param graphics context to draw in
     */
    public void render(GraphicsContext graphics) {
        Vector2 canvasPosition = getCanvasPosition();
        graphics.strokeOval(canvasPosition.x - radius, canvasPosition.y - radius, radius * 2, radius * 2);
    }

    /**
     * @return coordinates of center of the object in canvas coordinate system
     */
    protected Vector2 getCanvasPosition() {
        return new Vector2(position.x, world.getTerrain().height - 1 - position.y);
    }

    /**
     * All RoundObjects are compared by reference.
     * It is needed so <code>world</code> would remove them correctly.
     */
    @Override
    public final boolean equals(Object other) {
        return this == other;
    }

    /**
     * @return a newly constructed copy of an object's coordinates
     */
    public Vector2 getPositionCopy() {
        return new Vector2(position);
    }

    /**
     * Simulates fall by <code>FALLING_PRECISION</code> but <code>FALL_PER_STEP</code> times.
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
