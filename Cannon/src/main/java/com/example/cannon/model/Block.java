package com.example.cannon.model;

import java.util.function.IntConsumer;

public class Block {
    public static final int EMPTY = 0;
    public static final int GROUND = 1;
    public static final int OTHER = 2;

    public Block(int type) {
        this.type = type;
    }

    public boolean isFree() {
        return type == EMPTY;
    }

    public void updateType(int newType) {
        type = newType;
        if (onUpdate != null) {
            onUpdate.accept(type);
        }
    }

    public void onUpdateSet(IntConsumer onUpdate) {
        this.onUpdate = onUpdate;
    }

    public int getType() {
        return type;
    }

    private int type; // TODO enum
    private IntConsumer onUpdate;
}

