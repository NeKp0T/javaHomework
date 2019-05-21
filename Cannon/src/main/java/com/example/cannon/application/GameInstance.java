package com.example.cannon.application;

import com.example.cannon.GameException;
import com.example.cannon.implementations.PlayerUnit;
import com.example.cannon.model.Vector2;
import com.example.cannon.model.World;

import java.util.ArrayList;
import java.util.List;

class GameInstance {

    public GameInstance(int playerCount) throws GameException {
        this.world = new World();
        players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            double desirablePlayerX = world.getTerrain().width / (playerCount + 1) * (i + 1);
            Vector2 positionForPlayer = world.fitRound(PlayerUnit.RADIUS, desirablePlayerX);
            if (positionForPlayer == null) {
                throw new GameException("Could not find an appropriate position for player " + i);
            }
            players.add(new PlayerUnit(world, positionForPlayer));
            System.out.println(positionForPlayer.x + " " + positionForPlayer.y);
        }
    }

    /**
     * @return if game has not ended yet
     */
    public boolean nextMove() {
        int oldCurrentPlayerNumber = currentPlayerNumber;
        do {
            increaseCurrentPlayerNumber();
        } while(currentPlayerNumber != oldCurrentPlayerNumber && !players.get(currentPlayerNumber).isAlive());

        return currentPlayerNumber != oldCurrentPlayerNumber;
    }

    public void commandPlayer(Control command) {
        PlayerUnit currentPlayerUnit = players.get(currentPlayerNumber);
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
        }
    }


    public final World world;

    private int turn = 0;
    private List<PlayerUnit> players;
    private int currentPlayerNumber;

    private void increaseCurrentPlayerNumber() {
        currentPlayerNumber = (currentPlayerNumber + 1) % players.size();
    }

    public enum Control {
        AIM_LEFT, AIM_RIGHT, LEFT, RIGHT, FIRE;
    }
}
