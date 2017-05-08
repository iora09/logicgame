package com.bob.game.levels;

import com.badlogic.gdx.utils.XmlReader;
import com.bob.game.inputs.Block;

public abstract class Level {

    protected int[][] floor;
    protected int[][] objects;
    protected int coordX;
    protected int coordY;
    protected int noRules;
    protected Block[] inputs;
    protected Block[][] rules;
    protected String[] tutorialImages;
    protected String[] hints;
    protected String text;
    protected String levelName;
    protected String type;

    protected Level next;

    public Level(XmlReader.Element root, String levelName) {
        this.levelName = levelName;
        XmlReader.Element bobNode = root.getChildByName("bob");

        this.floor = csvToArray(root.getChildByName("floor").getText());
        this.objects = csvToArray(root.getChildByName("object").getText());
        this.coordX = bobNode.getInt("x");
        this.coordY = bobNode.getInt("y");
        this.text = root.getChildByName("text").getText();
        this.hints = extractStrings(root.getChildByName("hints"));
        this.tutorialImages = extractStrings(root.getChildByName("tutorial"));
        this.type = root.getAttribute("type");
        this.noRules = 8;
        this.inputs = new Block[]{};
        this.rules = new Block[][]{};
    }

    public abstract void save();

    public Level getNext() {
        return next;
    };

    public String getLevelName() {
        return levelName;
    }

    public Boolean allowMacro() {
        return false;
    }

    public Boolean allowRuleReset() {
        return true;
    }

    public Boolean hasTutorial() {
        return tutorialImages.length > 0;
    }

    public Boolean hasHints() {
        return hints.length > 0;
    }

    public Block[] getInputs() {
        return inputs;
    }

    public int getNoRules() {
        return noRules;
    }

    public int[][] getFloor() {
        return floor;
    }

    public int[][] getObjects() {
        return objects;
    }

    public int getX() {
        return coordX;
    }

    public int getY() {
        return coordY;
    }

    public String getText() {
        return text;
    }

    public Block[][] getRules() {
        return rules;
    }

    public String[] getTutorialImages() {
        return tutorialImages;
    }

    public void setNext(Level next) {
        this.next = next;
    }

    public String[] getHints() {
        return hints;
    }

    private int[][] csvToArray(String csv) {
        String[] lines = csv.split("\n");
        int[][] res = new int[lines.length][];

        for (int i = 0; i < lines.length; i++) {
            res[i] = new int[lines.length];
        }

        for (int i = 0; i < lines.length; i++) {
            String[] cols = lines[(lines.length - 1) - i].split(",");
            for(int j = 0; j < lines.length; j++) {
                res[j][i] = Integer.parseInt(cols[j].trim());
            }
        }

        return res;
    }

    private String[] extractStrings(XmlReader.Element element) {
        if (element == null) return new String[0];

        String[] res = new String[element.getChildCount()];

        for (int i = 0; i < res.length; i++) {
            res[i] = element.getChild(i).getText();
        }

        return res;
    }

    protected Block[] extractBlocks(XmlReader.Element blockContainer) {
        if (blockContainer == null) return new Block[0];

        int n = blockContainer.getChildCount();
        Block[] res = new Block[n];

        for(int i = 0; i < n; i++) {
            res[i] = Block.getBlock(blockContainer.getChild(i).getAttribute("name"));
        }

        return res;
    }

    public String getType() {
        return type;
    }
}
