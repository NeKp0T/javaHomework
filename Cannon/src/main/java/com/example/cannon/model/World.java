package com.example.cannon.model;

import com.sun.javafx.collections.ImmutableObservableList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class World {
    private Terrain terrain;
    private List<RoundObject> objects = new ArrayList<>();
    private List<Unit> units = new ArrayList<>();

    /**
     * Gravity acceleration per tick
     */
    private double g = 0.2;

    /**
     * Generates a sample world.
     */
    public World() {
        terrain = Terrain.constructSinusoidalTerrain(1000, 720);
    }

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

    public Terrain getTerrain() {
        return terrain;
    }

    public List<RoundObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * @param position position to find the closest unit to
     * @return closest unit of <code>null</code> if no such units found.
     */
    public @Nullable Unit getClosestUnit(Vector2 position) {
        Unit result = null;
        double resultDeltaSq = (terrain.height + terrain.width) * (terrain.height + terrain.width); // more than anything
        for (var unit : units) {
            double unitDeltaSq = position.difference(unit.position).lengthSq();
            if (resultDeltaSq > unitDeltaSq) {
                resultDeltaSq = unitDeltaSq;
                result = unit;
            }
        }
        return result;
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

    void addUnit(Unit unit) {
        units.add(unit);
    }

    void remove(RoundObject obj) {
        objects.remove(obj);
        if (obj instanceof Unit)
            units.remove(obj);
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
}
