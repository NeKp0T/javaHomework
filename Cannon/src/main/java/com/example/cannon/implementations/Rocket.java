package com.example.cannon.implementations;

import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;
import com.example.cannon.model.World;

public class Rocket extends OwnedProjectile {
    public Rocket(Vector2 position, World world, Vector2 speed, Unit owner) {
        super(3, owner.getPositionCopy(), world, speed, owner);
    }

    @Override
    protected void onStop() {
        System.out.println("Boom!");
        world.dealDamageInRadius(position, 20, 50);
        world.dealDamageInRadius(position, 10, 50);
        world.getTerrain().destroyInRadius(position, 20);
    }
}
