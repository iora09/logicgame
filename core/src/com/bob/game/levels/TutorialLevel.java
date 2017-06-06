package com.bob.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.XmlReader;
import com.bob.game.inputs.Block;

import java.util.HashMap;
import java.util.Map;


public class TutorialLevel extends Level {

    public TutorialLevel(XmlReader.Element root, String levelName) {
        super(root, levelName);
        this.choices = extractChoices(root.getChildByName("choices"));
        this.noRules = root.getChildByName("rules").getIntAttribute("available");
    }

    private Map<Block[], Boolean> extractChoices(XmlReader.Element choicesElement) {
        Map<Block[], Boolean> choices = new HashMap<>();
        int n = choicesElement.getChildCount();
        for (int i = 0; i < n; ++ i) {
            Block[] block = extractBlocks(choicesElement.getChild(i).getChildByName("rule"));
            choices.put(block, choicesElement.getChild(i).getChildByName("correct").getBoolean("value"));
        }
        return choices;
    }

    @Override
    public void save() {
        Preferences prefs = Gdx.app.getPreferences("Progress");
        prefs.putInteger("tutorialProgress", LevelFactory.TUTORIAL.indexOf(this));
        prefs.flush();
    }

    @Override
    public Boolean allowTutorial() {
        return true;
    }
}
