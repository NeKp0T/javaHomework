package com.example.cannon.model;

import com.example.cannon.GameException;

public abstract class Projectile extends RoundObject {
    private final static double COLLISION_CHECK_PRECISION = 3;

    private Vector2 speed;

    public Projectile(int radius, Vector2 position, World world, Vector2 speed) {
        super(radius, position, world);
        this.speed = new Vector2(speed);
    }

    /**
     * Moves projectile on <code>speed</code> vector in the world.
     *
     * Checks collisions and destroys projectile by calling <code>stop()</code> if
     * any collision requires.
     *
     * @return if it still exists in the world.
     */
    protected boolean moveOneStep() {
        if (!isInWorld()) {
            throw new RuntimeException("Trying to move a destroyed projectile");
        }
        int parts = timesToCheckCollision();
        Vector2 positionChange = speed.divided(parts);
        for (int i = 0; i < parts; i++) {
            if (checkCollisions()) {
                stop();
                return true;
            }
            position.add(positionChange);
        }
        if (checkCollisions()) {
            stop();
            return true;
        }

        speed.y -= world.getG();
        return true;
    }

    private void stop() {
        onStop();
        unregister();
    }

    private boolean checkCollisions() {
        Unit closest = world.getClosestUnit(position);
        if (closest != null) {
            if (closest.position.difference(position).lengthSq() <= (radius * radius)) {
                if (onUnitCollision(closest)) {
                    return true;
                }
            }
        }
        if (world.getTerrain().detectCollisionCircle(position, radius)) {
            return onTerrainCollision();
        }
        return false;
    }

    /**
     * @return <code>true</code> if projectile should be destroyed after collision
     */
    protected abstract boolean onTerrainCollision();

    /**
     * @param unit a unit projectile collided with
     * @return <code>true</code> if projectile should be destroyed after collision
     */
    protected abstract boolean onUnitCollision(Unit unit);

    /**
     * What to do when ceasing to exist.
     */
    protected abstract void onStop();

    /**
     * Describes how many times per one tick movement collision should be checked.
     *
     * Guarantees that <code>speed.length()</code> divided by return value
     * will be less then <code>COLLISION_CHECK_PRECISION</code>
     *
     * @return number of times to check collision per one transition on <code>speed</code> vector
     */
    private int timesToCheckCollision() {
        return (int) Math.ceil(speed.length() / COLLISION_CHECK_PRECISION);
    }

    @Override
    public boolean physicsStep() {
        return moveOneStep();
    }
}
