package com.lps.game;

public class LogicBrick {
    private String LPSString;
    private String imageName;
    private Type type;


    public LogicBrick(String lps, String image, Type type) {
        this.LPSString = lps;
        this.imageName = image;
        this.type = type;
    }

    public String getLPSString() {
        return LPSString;
    }

    public String getImageName() {
        return imageName;
    }

    enum Type {FLUENT, AND, NOT, IMPLY, CONSEQUENT}
}
