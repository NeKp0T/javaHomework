package com.example.cannon.model;

public class Unit extends MovingObject {
    private int hp;
    private int maxhp;
    private boolean alive = true;

    public Unit(int radius, int climbHeight, Vector2 position, World world, int maxhp, int speed) { // TODO assert maxhp > 0
        super(radius, climbHeight, position, world, speed);
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

    public void heal(int amount) {
        if (isAlive() && amount > 0 && hp <= maxhp) {
            hp += amount;
            if (hp > maxhp)
                hp = maxhp;
        }
    }

    protected void die() {
        alive = false;
    }
}
