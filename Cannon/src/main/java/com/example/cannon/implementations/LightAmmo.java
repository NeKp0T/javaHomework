package com.example.cannon.implementations;

import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;

/**
 * A light projectile that is less affected by gravity
 */
public class LightAmmo extends OwnedProjectile {

    private static final double SPEED = 7;
    private static final int RADIUS = 3;

    /**
     * Makes specified unit fire a light projectile
     */
    public static void fire(Unit owner) {
        new LightAmmo(owner);
    }

    /**
     * Constructs a new light projectile with specified owner
     */
    protected LightAmmo(Unit owner) {
        super(RADIUS, new Vector2(SPEED, 0), owner);
    }

    /**
     * Projectile explodes dealing damage in area.
     * Closer units receive more damage
     */
    @Override
    protected void onStop() {
        getWorld().dealDamageInRadius(position, 20, 25);
        getWorld().dealDamageInRadius(position, 10, 25);
    }

    /**
     * It's so light, gravity affects it twice less
     */
    @Override
    protected boolean moveOneStep() {
        boolean returnValue = super.moveOneStep();
        if (isInWorld()) {
            velocity.y += getWorld().getG() / 2; // take back half of gravity effect
        }
        return returnValue;
    }
}
