package com.example.cannon.model;

class Block {
    int type; // TODO enum

    public Block(int type) {
        this.type = type;
    }

    public Block() {}

    public boolean isFree() {
        return type == 0;
    }
}
