package com.example.cannon.model;

import javafx.scene.paint.Color;

import java.util.function.Consumer;

/**
 * Describes a single block of terrain.
 */
class Block {

    /**
     * Constructs a new block with provided type
     */
    public Block(BlockType type) {
        this.type = type;
    }

    /**
     * @return if objects can travel through a block
     */
    public boolean isFree() {
        return type.passable;
    }

    /**
     * Sets new type to a block
     */
    public void updateType(BlockType newType) {
        type = newType;
        if (onUpdate != null) {
            onUpdate.accept(type);
        }
    }

    /**
     * @return type of a block
     */
    public BlockType getType() {
        return type;
    }

    /**
     * Sets a function to be called then block type is changed.
     * Actually it is used only by terrain to set canvas updater.
     */
    void onUpdateSet(Consumer<BlockType> onUpdate) {
        this.onUpdate = onUpdate;
    }

    private BlockType type; // TODO enum
    private Consumer<BlockType> onUpdate;

    public enum BlockType {
        EMPTY(true, Color.TRANSPARENT),
        GROUND(false, Color.LIGHTGRAY),
        WALL(false, Color.BROWN),
        BACKGROUND(true, Color.LIGHTGREEN)
        ;
        public final boolean passable;
        public final Color color;
        BlockType(boolean passable, Color color) {
            this.passable = passable;
            this.color = color;
        }
    }
}

