package com.example.cannon.implementations;

import com.example.cannon.model.Projectile;
import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;

/**
 * A projectile that has a Unit owner with whom it does not collide.
 * It rotates itself by ut's owner angle when created.
 */
public abstract class OwnedProjectile extends Projectile {
    /**
     * An owner of this projectile
     */
    private final Unit owner;

    /**
     * Constructs new OwnedProjectile
     * @param speed vector which will be rotated by owner.getAngle() to get initial velocity
     * @param owner an owner of constructed projectile
     */
    protected OwnedProjectile(int radius, Vector2 speed, Unit owner) {
        super(radius, owner.getPositionCopy(), owner.getWorld(), speed.rotated(owner.getAngle()));
        this.owner = owner;
    }

    /**
     * It should return <code>true</code> if projectile wants to explode
     * @return always true in this implementation
     */
    @Override
    protected final boolean onTerrainCollision() {
        return true;
    }

    /**
     * It should return <code>true</code> if projectile wants to explode
     * @return returns <code>true</code> if unit it collided with is not it's owner
     */
    @Override
    protected final boolean onUnitCollision(Unit unit) {
        return unit != owner;
    }

}
