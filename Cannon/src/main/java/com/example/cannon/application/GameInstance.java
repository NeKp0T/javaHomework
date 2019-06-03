package com.example.cannon.application;

import com.example.cannon.implementations.PlayerUnit;
import com.example.cannon.model.RoundObject;
import com.example.cannon.model.Vector2;
import com.example.cannon.model.World;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an instance of a game which can be controlled from
 * outside without knowing anything about the game.
 * It provides an interface to get some information about it thought.
 */
public class GameInstance {

    /**
     * Constructs a new GameInstance with provided number of players and a default world.
     * @param playerCount number of players in the game
     * @throws WorldCreationException if could not construct a world with specified parameters
     */
    public GameInstance(int playerCount) throws WorldCreationException {
        this.world = new World();
        players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            double desirablePlayerX = world.getTerrain().width / (playerCount + 1) * (i + 1);
            Vector2 positionForPlayer = world.fitRound(PlayerUnit.RADIUS, desirablePlayerX);
            if (positionForPlayer == null) {
                throw new WorldCreationException("Could not find an appropriate position for player " + i);
            }
            players.add(new PlayerUnit(world, positionForPlayer));
            System.out.println(positionForPlayer.x + " " + positionForPlayer.y);
        }
    }

    /**
     * Changes current player.
     */
    private void nextMove() {
        int oldCurrentPlayerNumber = currentPlayerNumber;
        System.out.println("old " + oldCurrentPlayerNumber); // TODO delete
        do {
            increaseCurrentPlayerNumber();
        } while (currentPlayerNumber != oldCurrentPlayerNumber && !players.get(currentPlayerNumber).isAlive());
        System.out.println("next " + currentPlayerNumber); // TODO delete
    }

    /**
     * Sends a command to current player.
     * @param command command to send
     */
    public void commandPlayer(Control command) {
        PlayerUnit currentPlayerUnit = players.get(currentPlayerNumber);
        if (!currentPlayerUnit.isAlive()) {
            return;
        }
        switch (command) {

            case AIM_LEFT:
                currentPlayerUnit.rotateLeft();
                break;
            case AIM_RIGHT:
                currentPlayerUnit.rotateRight();
                break;
            case LEFT:
                currentPlayerUnit.tryMoveLeft();
                break;
            case RIGHT:
                currentPlayerUnit.tryMoveRight();
                break;
            case FIRE:
                currentPlayerUnit.fire();
                nextMove();
                break;
            case SELECT_0:
                currentPlayerUnit.selectWeapon(0);
                break;
            case SELECT_1:
                currentPlayerUnit.selectWeapon(1);
                break;
            case SELECT_2:
                currentPlayerUnit.selectWeapon(2);
                break;
        }
    }

    private void endGame() {
        winner = currentPlayerNumber;
    }

    /**
     * Simulates a step of world physics and tells whether it needs to be simulated further.
     * @return <code>true</code> if simulation should be called again
     */
    public boolean worldPhysicsStep() {
        boolean needNextStep = world.physicsStep();

        List<PlayerUnit> alive = players.stream().filter(PlayerUnit::isAlive).collect(Collectors.toList());
        if (winner == -1 && alive.size() == 0) {
            winner = -2;
        }
        if (alive.size() == 1) {
            winner = players.indexOf(alive.get(0));
        }

        return needNextStep;
    }


    private final World world;

    private final List<PlayerUnit> players;
    private int currentPlayerNumber;

    private int winner = -1;

    private void increaseCurrentPlayerNumber() {
        currentPlayerNumber = (currentPlayerNumber + 1) % players.size();
    }

    /**
     * Binds all blocks of terrain to provided canvas so that this canvas always represent
     * actual blocks state
     * Canvas is expected to match dimensions of a terrain.
     */
    public void setCanvas(Canvas terrainCanvas) {
        world.getTerrain().setCanvas(terrainCanvas);
    }

    /**
     * A getter for a terrain height
     * @return height of a game world
     */
    public double getTerrainHeight() {
        return world.getTerrain().height;
    }

    /**
     * A getter for a terrain width
     * @return width of a game world
     */
    public double getTerrainWidth() {
        return world.getTerrain().width;
    }

    /**
     * Renders all objects of the world on a provided GraphicsContext
     */
    public void renderObjects(GraphicsContext context) {
        for (RoundObject obj : world.getObjects()) {
            obj.render(context);
        }
    }

    /**
     * Provides information about whether the game has ended and who won.
     * @return winner player number, <code>-2</code> if a game ended in a
     * draw or <code>-1</code> if the game has not ended yet
     */
    public int getWinner() {
        return winner;
    }

    /**
     * A enum representing commands sent to a game by a player
     */
    public enum Control {
        AIM_LEFT, AIM_RIGHT, LEFT, RIGHT, FIRE, SELECT_0, SELECT_1, SELECT_2
    }

}
