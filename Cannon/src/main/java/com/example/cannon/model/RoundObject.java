package com.example.cannon.model;

class RoundObject {
    private final int radius;
    private final int climbHeight;
    private final Terrain terrain;
    private boolean affectedByGravity = true;

    Vector2 position;


    RoundObject(int radius, int climbHeight, Vector2 position, World world) {
        this.position = position;
        this.radius = radius;
        this.terrain = world.getTerrain();
        this.climbHeight = climbHeight;

        world.addObject(this);
    }

    private boolean intersects() {
        return terrain.detectCollisionCircle(position, radius);
    }

    public boolean fallOneStep() {
        if (!affectedByGravity || intersects()) {
            return false;
        }

        boolean fell = false;
        position.x--;
        while (!intersects()) {
            position.x--;
            fell = true;
        }
        position.x++;
        return fell;
    }

    public boolean tryMoveRight() {
        return tryMove(1);
    }

    public boolean tryMoveLeft() {
        return tryMove(-1);
    }

    private boolean tryMove(int dx) {
        var positionBefore = new Vector2(position);
        position.x += dx;
        for (int dy = 0; dy < climbHeight && intersects(); dy++);

        if (intersects()) {
            position = positionBefore;
            return false;
        }
        return true;
    }

}
