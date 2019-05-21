package com.example.cannon.implementations;

import com.example.cannon.model.Unit;
import com.example.cannon.model.Vector2;
import com.example.cannon.model.World;

import java.util.List;
import java.util.function.Consumer;

/**
 * A unit that players control. Unlike regular unit it can shoot
 */
public class PlayerUnit extends Unit {
    public static final int MAXHP = 100;
    public static final int RADIUS = 10;
    public static final int CLIMB_HEIGHT = 4;
    public static final int PLAYER_SPEED = 2;

    private int selectedWeaponId = 0;

    private static final List<Consumer<? super PlayerUnit>> arsenal = List.of(Bomb::fire, Rocket::fire);

    /**
     * Constructs a new PlayerUnit in provided world in a specified position.
     */
    public PlayerUnit(World world, Vector2 position) {
        super(RADIUS, CLIMB_HEIGHT, position, world, MAXHP, PLAYER_SPEED);
    }

    /**
     * Makes playerUnit fire it's selected weapon
     */
    public void fire() {
        arsenal.get(selectedWeaponId).accept(this);
    }

    /**
     * If provided number is a valid index in unit's weapon collection makes unit select
     * this weapon
     * @param weaponToSelect id of weapon to select in playerUnit's arsenal
     */
    public void selectWeapon(int weaponToSelect) {
        if (weaponToSelect >= 0 && weaponToSelect < arsenal.size()) {
            selectedWeaponId = weaponToSelect;
        }
    }
}
