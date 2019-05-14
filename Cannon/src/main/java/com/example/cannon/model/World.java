package com.example.cannon.model;

import java.util.ArrayList;
import java.util.List;

public class World {
    private Terrain terrain;
    private List<RoundObject> objects = new ArrayList<>();
    private List<Unit> units = new ArrayList<>();

    /**
     * Generates a sample world.
     */
    World() {
        terrain = new Terrain(1000, 1000);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void dealDamageInRadius(Vector2 center, double radius, int damage) {
        for (var unit : units) {
            if (unit.position.difference(center).lengthSq() <= radius * radius) {
                unit.receiveDamage(damage);
            }
        }
    }

    void addObject(RoundObject obj) {
        objects.add(obj);
    }

    void addUnit(Unit obj) {
        units.add(obj);
    }
}
