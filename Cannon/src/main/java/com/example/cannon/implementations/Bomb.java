package com.example.cannon.implementations;

import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;

/**
 * A slow big and heavy explosive projectile
 */
public class Bomb extends OwnedProjectile {
    private static final double SPEED = 5;
    private static final int RADIUS = 7;

    /**
     * Makes specified unit fire a bomb
     */
    public static void fire(Unit owner) {
        new Bomb(owner);
    }

    /**
     * Constructs a new bomb with specified owner
     */
    protected Bomb(Unit owner) {
        super(RADIUS, new Vector2(SPEED, 0), owner);
    }

    /**
     * Bomb explodes destroying ground and dealing damage in area.
     * Closer units receive more damage
     */
    @Override
    protected void onStop() {
        getWorld().dealDamageInRadius(position, 25, 50);
        getWorld().dealDamageInRadius(position, 13, 50);
        getWorld().getTerrain().destroyInRadius(position, 25);
    }
}
