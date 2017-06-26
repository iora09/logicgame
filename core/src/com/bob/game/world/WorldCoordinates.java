package com.bob.game.world;

import com.bob.main.Config;

public class WorldCoordinates {
    private float x;
    private float y;

    public WorldCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getWorldX() {
        return this.x;
    }

    public float getWorldY() {
        return this.y;
    }

    public float getScreenX() {
        return -720 + (this.x + this.y) * Config.tileWidth/2;
    }

    public float getScreenY() {
        return 500 - (this.x - this.y) * Config.tileHeight/2;
    }

    public void increaseX(float dx) {
        this.x += dx;
    }

    public void increaseY(float dy) {
        this.y += dy;
    }

    public boolean collide(WorldCoordinates coord) {
        return Math.round(this.x) == Math.round(coord.getWorldX()) && Math.round(this.y) == Math.round(coord.getWorldY());
    }

    public boolean isInMap() {
        return (getScreenX() > 50 && getScreenX() < 1335 && getScreenY() > 120 && getScreenY() < 885);
    }
}
