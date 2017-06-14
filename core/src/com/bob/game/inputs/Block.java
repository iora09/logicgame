package com.bob.game.inputs;

public enum Block {

    // Colors
    WHITE("white(X,Y)", "white", Type.FLUENT, "If Bob is on a white cell"),
    RED("red(X,Y)", "red", Type.FLUENT, "If Bob is on a red cell"),
    YELLOW("yellow(X,Y)", "yellow", Type.FLUENT, "If Bob is on a yellow cell"),
    GREEN("green(X,Y)", "green", Type.FLUENT, "If Bob is on a green cell"),
    ORANGE("orange(X,Y)", "orange", Type.FLUENT, "If Bob is on a orange cell"),
    PURPLE("purple(X,Y)", "purple", Type.FLUENT, "If Bob is on a purple cell"),

    //Colors PREV
    WHITE_PREV("white(U,V)", "white_prev", Type.FLUENT, "If Bob was previously on a white cell"),
    RED_PREV("red(U,V)", "red_prev", Type.FLUENT, "If Bob was previously on a red cell"),
    YELLOW_PREV("yellow(U,V)", "yellow_prev", Type.FLUENT, "If Bob was previously on a yellow cell"),
    GREEN_PREV("green(U,V)", "green_prev", Type.FLUENT, "If Bob was previously on a green cell"),
    ORANGE_PREV("orange(U,V)", "orange_prev", Type.FLUENT, "If Bob was previously on a orange cell"),
    PURPLE_PREV("purple(U,V)", "purple_prev", Type.FLUENT, "If Bob was previously on a purple cell"),

    //Colors NORTH
    WHITE_NORTH("white(X,P)", "white_north", Type.FLUENT, "If to the North of Bob there's a white cell"),
    RED_NORTH("red(X,P)", "red_north", Type.FLUENT, "If to the North of Bob there's a red cell"),
    YELLOW_NORTH("yellow(X,P)", "yellow_north", Type.FLUENT, "If to the North of Bob there's a yellow cell"),
    GREEN_NORTH("green(X,P)", "green_north", Type.FLUENT, "If to the North of Bob there's a green cell"),
    ORANGE_NORTH("orange(X,P)", "orange_north", Type.FLUENT, "If to the North of Bob there's a orange cell"),
    PURPLE_NORTH("purple(X,P)", "purple_north", Type.FLUENT, "If to the North of Bob there's a purple cell"),
    WATER_NORTH("water(X,P)", "water_north", Type.FLUENT, "If to the North of Bob there's water."),

    //Colors SOUTH
    WHITE_SOUTH("white(X,N)", "white_south", Type.FLUENT, "If to the South of Bob there's a white cell"),
    RED_SOUTH("red(X,N)", "red_south", Type.FLUENT, "If to the South of Bob there's a red cell"),
    YELLOW_SOUTH("yellow(X,N)", "yellow_south", Type.FLUENT, "If to the South of Bob there's a yellow cell"),
    GREEN_SOUTH("green(X,N)", "green_south", Type.FLUENT, "If to the South of Bob there's a green cell"),
    ORANGE_SOUTH("orange(X,N)", "orange_south", Type.FLUENT, "If to the South of Bob there's a orange cell"),
    PURPLE_SOUTH("purple(X,N)", "purple_south", Type.FLUENT, "If to the South of Bob there's a purple cell"),
    WATER_SOUTH("water(X,N)", "water_south", Type.FLUENT, "If to the South of Bob there's water."),

    //Colors WEST
    WHITE_WEST("white(M,Y)", "white_west", Type.FLUENT, "If to the West of Bob there's a white cell"),
    RED_WEST("red(M,Y)", "red_west", Type.FLUENT, "If to the West of Bob there's a red cell"),
    YELLOW_WEST("yellow(M,Y)", "yellow_west", Type.FLUENT, "If to the West of Bob there's a yellow cell"),
    GREEN_WEST("green(M,Y)", "green_west", Type.FLUENT, "If to the West of Bob there's a green cell"),
    ORANGE_WEST("orange(M,Y)", "orange_west", Type.FLUENT, "If to the West of Bob there's a orange cell"),
    PURPLE_WEST("purple(M,Y)", "purple_west", Type.FLUENT, "If to the West of Bob there's a purple cell"),
    WATER_WEST("water(M,Y)", "water_west", Type.FLUENT, "If to the West of Bob there's water."),

    //Colors EAST
    WHITE_EAST("white(Q,Y)", "white_east", Type.FLUENT, "If to the East of Bob there's a white cell"),
    RED_EAST("red(Q,Y)", "red_east", Type.FLUENT, "If to the East of Bob there's a red cell"),
    YELLOW_EAST("yellow(Q,Y)", "yellow_east", Type.FLUENT, "If to the East of Bob there's a yellow cell"),
    GREEN_EAST("green(Q,Y)", "green_east", Type.FLUENT, "If to the East of Bob there's a green cell"),
    ORANGE_EAST("orange(Q,Y)", "orange_east", Type.FLUENT, "If to the East of Bob there's a orange cell"),
    PURPLE_EAST("purple(Q,Y)", "purple_east", Type.FLUENT, "If to the East of Bob there's a purple cell"),
    WATER_EAST("water(Q,Y)", "water_east", Type.FLUENT, "If to the East of Bob there's water."),

    // Directions
    LEFT("goLeft", "left", Type.CONSEQUENT, "Bob should go West"),
    RIGHT("goRight", "right", Type.CONSEQUENT, "Bob should go East"),
    UP("goUp", "up", Type.CONSEQUENT, "Bob should go North"),
    DOWN("goDown", "down", Type.CONSEQUENT, "Bob should go South"),

    // Other instructions
    WAIT("wait", "pause", Type.CONSEQUENT, "Bob should wait"),

    // Connectors
    AND("&", "and", Type.AND, "AND, to be used in: if a AND b"),
    IMPLY(" -> ", "imply", Type.IMPLY, "IMPLY/THEN, to be used in: if a THEN b"),
    NOT("!", "not", Type.NOT, "NOT, to be used in: NOT a");

    private final String LPSString;
    private final String imageName;
    private final Type type;
    private final String tooltip;

    Block(String lps, String image, Type type, String tooltip) {
        this.LPSString = lps;
        this.imageName = image;
        this.type = type;
        this.tooltip = tooltip;
    }

    public String getLPSString() {
        return LPSString;
    }

    public String getImageName() {
        return imageName;
    }

    public Type getType() {
        return type;
    }

    public String getTooltip() {
        return tooltip;
    }

    public static Block getBlock(String name) {
        for (Block b: Block.values()) {
            if (b.getImageName().equals(name)) return b;
        }
        return null;
    }
}
