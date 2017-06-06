package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.InputsManager;
import com.bob.game.inputs.Rule;
import java.util.HashMap;
import java.util.Map;

public class ChoicesManager extends InputsManager{

    Map<Rule, Boolean> choices = new HashMap<>();

    public void setupRules(int noRules, Skin skin, int startingX, int startingY) {
        Rule[] rules = choices.keySet().toArray(new Rule[choices.keySet().size()]);
        for(int i = 0; i < rules.length; ++i) {
            this.rules[i] = rules[i];
        }
        initRuleView(skin, startingX, startingY);
        setupRuleView(noRules, false);
    }

    public void setupChoices(Map<Block[], Boolean> choices) {
        for(Block[] block : choices.keySet()) {
            Rule rule = new Rule();
            rule.setRuleBlocks(block);
            this.choices.put(rule, choices.get(block));
        }
    }


}
