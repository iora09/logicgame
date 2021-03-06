package com.bob.game.levels;

import com.badlogic.gdx.utils.XmlReader;
import com.bob.game.inputs.Block;

import java.sql.Date;
import java.util.Map;

public abstract class Level {

    private int[][] floor;
    protected int[][] objects;
    private int coordX;
    private int coordY;
    protected int noRules;
    protected Block[] inputs;
    protected Block[][] rules;
    protected Map<Block[], Boolean> choices;
    protected String[] tutorialImages;
    protected String[] hints;
    protected String text;
    protected String tutText;
    protected String levelName;
    protected String type = "";
    protected String owner = "";

    protected Level next;
    private Date date;

    public Level(XmlReader.Element root, String levelName) {
        this.levelName = levelName;
        this.type = root.getAttribute("type");
        XmlReader.Element bobNode = root.getChildByName("bob");

        this.floor = csvToArray(root.getChildByName("floor").getText());
        this.objects = csvToArray(root.getChildByName("object").getText());
        this.coordX = bobNode.getInt("x");
        this.coordY = bobNode.getInt("y");
        if (root.getChildByName("text") != null) {
            this.text = root.getChildByName("text").getText();
        }
        this.hints = extractStrings(root.getChildByName("hints"));
        this.tutorialImages = extractStrings(root.getChildByName("tutorial"));

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

    public Boolean allowTutorial() {
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

    public void setText(String text) {
        this.text = text;
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

    public String getType() {
        return type;
    }

    public Map<Block[], Boolean> getChoices() {
        return choices;
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
       return extractBlocksFromPosition(blockContainer, 0);
    }

    protected Block[] extractBlocksFromPosition(XmlReader.Element blockContainer, int pos) {
        if (blockContainer == null) return new Block[0];

        int n = blockContainer.getChildCount();
        Block[] res = new Block[n - pos];

        for(int i = pos; i < n; i++) {
            res[i - pos] = Block.getBlock(blockContainer.getChild(i).getAttribute("name"));
        }

        return res;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public String getTutText() {
        return tutText;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void resetTutorials() {
        this.tutorialImages = new String[] {
                };
    }
}
