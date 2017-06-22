package com.bob.game.inputs;

public enum Tile {

    WHITE("white_tile", 1),
    RED("red_tile", 2),
    YELLOW("yellow_tile", 3),
    GREEN("green_tile", 4),
    PURPLE("purple_tile", 5),
    ORANGE("orange_tile", 6),
    GOLDEN("golden_tile", 7),
    QUESTION("question_tile", 8),
    WATER("water_tile", 9);

    private String imageName;
    private int xmlNumber;

    Tile(String imageName, int xmlNumber) {
        this.imageName = imageName;
        this.xmlNumber = xmlNumber;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return "maps/" + imageName + ".png";
    }
    public int getXmlNumber() {
        return xmlNumber;
    }

    public static Tile getTile(String name) {
        for (Tile t: Tile.values()) {
            if (t.getImageName().equals(name)) return t;
        }
        return null;
    }

    public static Tile getTile(int xmlNumber) {
        for (Tile t: Tile.values()) {
            if (t.getXmlNumber() == xmlNumber) return t;
        }
        return null;
    }

}
