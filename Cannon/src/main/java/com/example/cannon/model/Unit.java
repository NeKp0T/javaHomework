package com.example.cannon.model;

public class Unit extends RoundObject {
    private int hp;
    private int maxhp;
    private boolean alive = true;

    Unit(int radius, int climbHeight, Vector2 position, World world, int maxhp) { // TODO assert maxhp > 0
        super(radius, climbHeight, position, world);
        this.maxhp = maxhp;
        hp = maxhp;
        world.addUnit(this);
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean receiveDamage(int amount) {
        if (isAlive() && amount > 0) {
            hp -= amount;
        }
        if (hp < 0) {
            die();
        }
        return isAlive();
    }

    private void die() {
        alive = false;
    }
}
