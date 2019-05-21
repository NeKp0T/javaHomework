package com.example.cannon.model;

/**
 * A unit that has hitpoints and in registred in the world differently separately.
 */
public class Unit extends MovingObject {
    private int hp;
    private int maxhp;
    private boolean alive = true;

    /**
     * Constructs a new Unit at full hp
     * @param maxhp maximum hp a unit can have
     * @param speed speed as in MovingObject
     */
    public Unit(int radius, int climbHeight, Vector2 position, World world, int maxhp, int speed) { // TODO assert maxhp > 0
        super(radius, climbHeight, position, world, speed);
        this.maxhp = maxhp;
        hp = maxhp;
        world.addUnit(this);
    }

    /**
     * @return if this unit alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * @param amount amount of demage to receive
     * @return if this unit dead after receiving damage
     */
    public boolean receiveDamage(int amount) {
        System.out.println("Received " + amount + " damage");
        if (isAlive() && amount > 0) {
            hp -= amount;
        }
        if (hp <= 0) {
            die();
        }
        return isAlive();
    }

    /**
     * Restores hp, but can not make hp higher than maxhp
     * @param amount amount of hp to restore
     */
    public void heal(int amount) {
        if (isAlive() && amount > 0 && hp <= maxhp) {
            hp += amount;
            if (hp > maxhp)
                hp = maxhp;
        }
    }

    /**
     * This method is called than a unit dies.
     * This implementation unregisters unit from the world, but subclasses
     * are free to redefine it.
     */
    protected void die() {
        System.out.println("Khghrhh...");
        alive = false;
        unregister();
    }
}
