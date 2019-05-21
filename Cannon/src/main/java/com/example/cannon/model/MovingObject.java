package com.example.cannon.model;

import javafx.scene.canvas.GraphicsContext;

/**
 * Game object that can move on surfaces and has a direction of sight.
 */
public class MovingObject extends RoundObject {
    /**
     * By how much an object rotates in one rotate call.
     */
    private static final double ROTATE_PRECISION = Math.toRadians(5);

    private final int climbHeight;
    private final int speed;

    private boolean facesRight = true;
    protected double angle = Math.toRadians(30);

    /**
     * Constructs new MovingObject
     * @param climbHeight how high can it climb when moving by 1 horizontally or fall without
     *                    finishing movement
     * @param speed how much it can move in one movement
     */
    MovingObject(int radius, int climbHeight, Vector2 position, World world, int speed) {
        super(radius, position, world);
        this.climbHeight = climbHeight;
        this.speed = speed;
    }

    /**
     * Makes object face right
     */
    public void orientRight() {
        if (!facesRight) {
            flipAngle();
            facesRight = true;
        }
    }

    /**
     * Makes object face left
     */
    public void orientLeft() {
        if (facesRight) {
            flipAngle();
            facesRight = false;
        }
    }

    private void flipAngle() {
        angle = Math.PI - angle;
    }

    /**
     * Tries to move a MovingObject by it's speed blocks to the right. Also makes object face right.
     * @return <code>false</code> if met an impassable obstruction or a deep enough pit
     */
    public boolean tryMoveRight() {
        orientRight();
        return tryMove(1);
    }

    /**
     * Tries to move a MovingObject by it's speed blocks to the left. Also makes object face left.
     * @return <code>false</code> if met an impassable obstruction or a deep enough pit
     */
    public boolean tryMoveLeft() {
        orientLeft();
        return tryMove(-1);
    }

    /**
     * Rotates object line of sight a bit to the left
     */
    public void rotateLeft() {
        angle += ROTATE_PRECISION;
    }
    /**
     * Rotates object line of sight a bit to the right
     */
    public void rotateRight() {
        angle -= ROTATE_PRECISION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);

        Vector2 canvasPosition = getCanvasPosition();
        Vector2 cannonEnd = new Vector2(radius * 1.3, 0).rotated(-angle).sum(canvasPosition);

        graphics.strokeLine(canvasPosition.x, canvasPosition.y, cannonEnd.x, cannonEnd.y);

        var vectorToFront = new Vector2(radius, 0);
        if (!facesRight) {
            vectorToFront.x = -vectorToFront.x;
        }
        Vector2 frontEnd = canvasPosition.sum(vectorToFront);
        Vector2 nearFrontEnd = canvasPosition.sum(vectorToFront.divided(2));
        graphics.strokeLine(nearFrontEnd.x, nearFrontEnd.y, frontEnd.x, frontEnd.y);
    }

    /**
     * @return angle of object's line of sight
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Tries to move object in the direction <code>dx</code>
     * @param dx direction to move. Expected to by +-1
     * @return <code>false</code> if an obstruction was met
     */
    private boolean tryMove(int dx) {
        for (int i = 0; i < speed; i++) {
            var positionBeforeMove = new Vector2(position);
            position.x += dx;
            for (int dy = 0; dy < climbHeight && detectTerrainCollision(); dy++) {
                position.y++;
            }

            if (detectTerrainCollision()) {
                position = positionBeforeMove;
                return false;
            }

            // if falls more than climbHeight, then interrupt movement
            var positionBeforeFall = new Vector2(position);
            while (fallOneStep() != 0 && (positionBeforeFall.y - position.y) < climbHeight);
            if ((positionBeforeFall.y - position.y) >= climbHeight) {
                position = positionBeforeFall;
                return false;
            }
        }
        return true;
    }
}
