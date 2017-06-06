package com.bob.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.XmlReader;

public class WriteLevel extends Level {

    public WriteLevel(XmlReader.Element root, String levelName) {
        super(root, levelName);

        this.noRules = root.getChildByName("rules").getIntAttribute("available");
        this.inputs = extractBlocks(root.getChildByName("inputs"));
    }

    @Override
    public void save() {
        Preferences prefs = Gdx.app.getPreferences("Progress");
        prefs.putInteger("writeProgress", LevelFactory.WRITE.indexOf(this));
        prefs.flush();
    }
}
