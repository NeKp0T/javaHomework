package com.example.cannon.implementations;

import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;

/**
 * Rocket is a fast-travelling projectile that is less affected by gravity
 */
public class Rocket extends OwnedProjectile {

    private static final double SPEED = 10;
    private static final int RADIUS = 3;

    /**
     * Makes specified unit fire a rocket
     */
    public static void fire(Unit owner) {
        new Rocket(owner);
    }

    /**
     * Constructs a new rocket with specified owner
     */
    protected Rocket(Unit owner) {
        super(RADIUS, new Vector2(SPEED, 0), owner);
    }

    /**
     * Rocket explodes destroying ground and dealing damage in area.
     * Closer units receive more damage
     */
    @Override
    protected void onStop() {
        getWorld().dealDamageInRadius(position, 20, 25);
        getWorld().dealDamageInRadius(position, 10, 25);
        getWorld().getTerrain().destroyInRadius(position, 20);
    }

    /**
     * Rocket moves generally like a projectile, but it's engines negate
     * a part of gravity effect
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
