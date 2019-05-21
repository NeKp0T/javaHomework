package com.example.cannon.model;

import javafx.scene.canvas.GraphicsContext;

public class MovingObject extends RoundObject {
    private static final double ROTATE_PRECISION = Math.toRadians(5);

    private final int climbHeight;
    private final int speed;

    MovingObject(int radius, int climbHeight, Vector2 position, World world, int speed) {
        super(radius, position, world);
        this.climbHeight = climbHeight;
        this.speed = speed;
    }

    public boolean tryMoveRight() {
        return tryMove(1);
    }

    public boolean tryMoveLeft() {
        return tryMove(-1);
    }

    public void rotateLeft() {
        angle += ROTATE_PRECISION;
    }
    public void rotateRight() {
        angle -= ROTATE_PRECISION;
    }

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
            var positionBeforeFall = position;
            while (fallOneStep() != 0 && (positionBeforeFall.y - position.y) < climbHeight);
            if ((positionBeforeFall.y - position.y) >= climbHeight) {
                position = positionBeforeFall;
                return true;
            }

        }
        return true;
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);

        Vector2 canvasPosition = getCanvasPosition();
        Vector2 cannonEnd = new Vector2(radius * 1.3, 0).rotated(-angle).sum(canvasPosition);

        graphics.strokeLine(canvasPosition.x, canvasPosition.y, cannonEnd.x, cannonEnd.y);
    }

    public double getAngle() {
        return angle;
    }

    protected double angle = Math.toRadians(90);
}
