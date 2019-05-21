package com.example.cannon.model;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a game world with terrain and all game objects in it
 */
public class World {
    private final Terrain terrain;
    private final List<RoundObject> objects = new ArrayList<>();
    private final List<Unit> units = new ArrayList<>();

    /**
     * Gravity acceleration per tick
     */
    private double g = 0.2;

    /**
     * Generates a sample world
     */
    public World() {
        terrain = Terrain.constructSinusoidalTerrain(1000, 720);
    }

    /**
     * Simulates one step of physics
     * @return if something changed in this step and further simulation is required
     */
    public boolean physicsStep() {
        boolean changed = false;

        // make a copy since during physics simulation some objects could have been destroyed
        var objectsCopy = new ArrayList<>(objects);
        for (RoundObject obj : objectsCopy) {
            if (obj.isInWorld()) {
                changed = changed || obj.physicsStep();
            }
        }
        return changed;
    }

    /**
     * @return terrain of a world
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * @return an unmodifiable collection of all game objects in this world.
     */
    public List<RoundObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * @param position position to find the closest unit to
     * @return closest unit or <code>null</code> if units found.
     */
    public @Nullable Unit getClosestUnit(Vector2 position) {
        Unit result = null;
        double resultDelta = terrain.height + terrain.width; // more than anything
        for (var unit : units) {
            double unitDelta = unit.getDistance(position);
            if (resultDelta > unitDelta) {
                resultDelta = unitDelta;
                result = unit;
            }
        }
        return result;
    }

    /**
     * Deals specified amount of damage in provided circular area.
     * Does not destroy blocks.
     */
    public void dealDamageInRadius(Vector2 center, double radius, int damage) {
        var copyOfUnits = new ArrayList<>(units);
        for (var unit : copyOfUnits) {
            if (unit.getDistance(center) <= radius) {
                unit.receiveDamage(damage);
            }
        }
    }

    /**
     * @return this world's gravity acceleration per tick
     */
    public double getG() {
        return g;
    }

    /**
     * Tries to find an appropriate position for a round object with provided radius with provided X coordinate
     * @return found position or null if could not find one
     */
    public Vector2 fitRound(int radius, double desirableX) {
        for (int dx = 0; dx < terrain.width; dx++) {
            for (int direction = -1; direction <= 1; direction += 2) {
                Vector2 position = new Vector2(desirableX + direction * dx, radius);
                for (int y = 0; y < terrain.height; y++) {
                    if (!terrain.detectCollisionCircle(position, radius)) {
                        return position;
                    }
                    position.y++;
                }
            }
        }

        return null;
    }

    /**
     * Adds a RoundObject in the world.
     * This function is called once for every object in RoundObject constructor
     * @param obj object to register
     */
    void addObject(RoundObject obj) {
        objects.add(obj);
    }

    /**
     * Registers a Unit as unit in the world. Does not register it as a RoundObject since
     * it is done in RoundObject constructor
     * This function is called once for every unit in Unit constructor
     * @param unit unit to register
     */
    void addUnit(Unit unit) {
        units.add(unit);
    }

    /**
     * Unregisters a RoundObject or a Unit from the world
     * @param obj RoundObject or a Unit to remove from the world
     */
    void remove(RoundObject obj) {
        objects.remove(obj);
        if (obj instanceof Unit)
            units.remove(obj);
    }
}
