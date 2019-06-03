package com.example.cannon.model;

import org.jetbrains.annotations.NotNull;

/**
 * A modifiable vector of two double values.
 * All the methods do not change base vector unless their docs specifically say so.
 */
public class Vector2 {
    public double x;
    public double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2(@NotNull Vector2 other) {
        x = other.x;
        y = other.y;
    }

    /**
     * Increases vector by provided vector
     * @return this
     */
    @NotNull
    public Vector2 add(@NotNull Vector2 other) {
        x += other.x;
        y += other.y;
        return this;
    }

    /**
     * Decreases vector by provided vector
     * @return this
     */
    @NotNull
    public Vector2 subtract(@NotNull Vector2 other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    @NotNull
    public Vector2 multiply(double mult) {
        x *= mult;
        y *= mult;
        return this;
    }

    @NotNull
    public Vector2 sum(@NotNull Vector2 other) {
        return new Vector2(this).add(other);
    }

    @NotNull
    public Vector2 difference(@NotNull Vector2 other) {
        return new Vector2(this).subtract(other);
    }

    @NotNull
    public Vector2 divided(double divider) {
        return new Vector2(x / divider, y / divider);
    }

    @NotNull
    public Vector2 multiplied(double mult) {
        return new Vector2(this).multiply(mult);
    }

    @NotNull
    public Vector2 rotated(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2(x * cos - y * sin, x * sin + y * cos);
    }

    /**
     * @return squared length of a vector
     */
    public double lengthSq() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }
}
