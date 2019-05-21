package com.example.cannon.implementations;

import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;
import com.example.cannon.model.World;

public class PlayerUnit extends Unit {
    public static final int MAXHP = 100;
    public static final int RADIUS = 10;
    public static final int CLIMB_HEIGHT = 4;
    public static final int PLAYER_SPEED = 4;

    public PlayerUnit(World world, Vector2 position) {
        super(RADIUS, CLIMB_HEIGHT, position, world, MAXHP, PLAYER_SPEED);
    }

    public Rocket fire() {
        return new Rocket(position, world, new Vector2(10, 0), this);
    }
}
