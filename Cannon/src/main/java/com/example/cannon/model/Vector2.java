package com.example.cannon.model;

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

    public Vector2(Vector2 other) {
        x = other.x;
        y = other.y;
    }

    public Vector2 add(Vector2 other) {
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector2 substract(Vector2 other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    public Vector2 sum(Vector2 other) {
        return new Vector2(this).add(other);
    }

    public Vector2 difference(Vector2 other) {
        return new Vector2(this).substract(other);
    }

    public Vector2 divided(double divider) {
        return new Vector2(x / divider, y / divider);
    }

    public Vector2 rotated(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2(x * cos - y * sin, x * sin + y * cos);
    }

    public double lengthSq() {
        return x*x + y*y;
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }
}
