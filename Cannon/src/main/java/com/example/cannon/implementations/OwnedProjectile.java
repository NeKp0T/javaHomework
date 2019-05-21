package com.example.cannon.implementations;

import com.example.cannon.model.Projectile;
import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;
import com.example.cannon.model.World;

public abstract class OwnedProjectile extends Projectile {
    private final Unit owner;

    public OwnedProjectile(int radius, Vector2 position, World world, Vector2 speed, Unit owner) {
        super(radius, position, world, speed.rotated(owner.getAngle()));
        this.owner = owner;
    }

    @Override
    protected final boolean onTerrainCollision() {
        return true;
    }

    @Override
    protected final boolean onUnitCollision(Unit unit) {
        return unit != owner;
    }

}
