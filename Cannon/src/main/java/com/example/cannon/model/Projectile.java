package com.example.cannon.model;


/**
 * A base class for projectiles.
 * Projectiles are fully physically simulated (i.e. have velocity affected by gravity)
 * which do something on collision and are expected to explode sometime
 */
public abstract class Projectile extends RoundObject {
    /**
     * Collision checks are performed at least each <code>COLLISION_CHECK_PRECISION</code> units of
     * projectile's trajectory
     */
    private final static double COLLISION_CHECK_PRECISION = 3;

    protected Vector2 velocity;

    /**
     * Constructs a new projectile with provided parameters
     * @param velocity initial velocity
     */
    public Projectile(int radius, Vector2 position, World world, Vector2 velocity) {
        super(radius, position, world);
        this.velocity = new Vector2(velocity);
    }

    /**
     * Moves projectile by <code>velocity</code> vector in the world.
     *
     * Checks collisions and destroys projectile with calling <code>onStop()</code> first if
     * any collision asks.
     *
     * @return if it still exists in the world.
     */
    protected boolean moveOneStep() {
        if (!isInWorld()) {
            throw new RuntimeException("Trying to move a destroyed projectile");
        }

        int parts = timesToCheckCollision();
        Vector2 positionChange = velocity.divided(parts);
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

        velocity.y -= getWorld().getG();
        return true;
    }

    private void stop() {
        onStop();
        unregister();
    }

    private boolean checkCollisions() {
        Unit closest = getWorld().getClosestUnit(position);
        if (closest != null) {
            if (closest.getDistance(position) <= radius) {
                if (onUnitCollision(closest)) {
                    return true;
                }
            }
        }
        if (getWorld().getTerrain().detectCollisionCircle(position, radius)) {
            return onTerrainCollision();
        }
        return false;
    }

    /**
     * This function is called every time projectile collides with terrain.
     * It should return <code>true</code> if projectile wants to explode
     * @return <code>true</code> if projectile should explode after collision
     */
    protected abstract boolean onTerrainCollision();

    /**
     * This function is called every time projectile collides with a unit.
     * Unit collisions are tested before terrain collisions.
     * It should return <code>true</code> if projectile wants to explode
     * @param unit a unit projectile collided with
     * @return <code>true</code> if projectile should explode after collision
     */
    protected abstract boolean onUnitCollision(Unit unit);

    /**
     * What to do when ceasing to exist (projectile is expected to explode in this function).
     * Do not call <code>unregister()</code> here! It is called automatically after collision.
     */
    protected abstract void onStop();

    /**
     * Describes how many times per one tick movement collision should be checked.
     *
     * Guarantees that <code>velocity.length()</code> divided by return value
     * will be less then <code>COLLISION_CHECK_PRECISION</code>
     *
     * @return number of times to check collision per one transition by <code>velocity</code> vector
     */
    private int timesToCheckCollision() {
        return (int) Math.ceil(velocity.length() / COLLISION_CHECK_PRECISION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean physicsStep() {
        return moveOneStep();
    }
}
