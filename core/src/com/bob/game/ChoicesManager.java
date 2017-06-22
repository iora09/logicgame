package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.InputsManager;
import com.bob.game.inputs.Rule;
import com.bob.main.TextureFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoicesManager extends InputsManager{

    private Map<Rule, Boolean> choices = new HashMap<>();
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private List<Image> results = new ArrayList<>();
    private boolean allCheckedRulesCorrect = true;
    private boolean correctRulesComplete = true;

    public void setupRules(int noRules, Skin skin, int startingX, int startingY) {
        Rule[] rules = choices.keySet().toArray(new Rule[choices.keySet().size()]);
        for(int i = 0; i < noRules; ++i) {
            this.rules[i] = rules[i];
        }
        initRuleView(skin, startingX, startingY, false);
        setupRuleView(noRules, false);
    }

    public void setupChoices(Map<Block[], Boolean> choices) {
        this.choices.clear();
        for(Block[] block : choices.keySet()) {
            Rule rule = new Rule();
            rule.setRuleBlocks(block);
            this.choices.put(rule, choices.get(block));
        }
    }

    public void setupCheckboxes(Skin skin, int noRules, int startingX, int startingY) {
        checkBoxList.clear();
        for (int i = 0; i < noRules; ++i) {
            CheckBox checkBox = new CheckBox(null, skin);
            checkBox.setBounds(startingX, startingY, 40, 50);
            checkBox.getImageCell().height(30f);
            checkBox.getImageCell().width(30f);
            checkBoxList.add(checkBox);
            layer.addActor(checkBoxList.get(i));
            startingY -= 70;
        }
    }

    public void resetCheckboxes() {
        allCheckedRulesCorrect = true;
        correctRulesComplete = true;
        for(CheckBox checkBox : checkBoxList) {
            if(checkBox.isChecked()) {
                checkBox.toggle();
            }
        }

        for(Image result : results) {
            layer.removeActor(result);
        }
    }


    public void checkChoices(int startingX, int startingY, int noRules) {
        results.clear();
        for (int i = 0 ; i < noRules; ++i) {
            if (choices.containsKey(rules[i])) {
                if (checkBoxList.get(i).isChecked()) {
                    if (choices.get(rules[i])) {
                        addResult(startingX, startingY, true);
                    } else {
                        addResult(startingX, startingY, false);
                        allCheckedRulesCorrect = false;
                    }
                } else {
                    if (choices.get(rules[i])) {
                        correctRulesComplete = false;
                    }
                }
                startingY -= 70;
            }
        }
    }

    private void addResult(int startingX, int startingY, boolean correctResult) {
        Image result;
        if (correctResult) {
            result =  new Image(TextureFactory.createTexture("buttons/green_check.png"));
        } else {
            result = new Image(TextureFactory.createTexture("buttons/red_x.png"));
        }
        results.add(result);
        results.get(results.size() - 1).setBounds(startingX, startingY, 35, 35);
        layer.addActor(results.get(results.size() - 1));
    }

    public boolean areAllCheckedRulesCorrect() {
        return allCheckedRulesCorrect;
    }

    public boolean areCorrectRulesComplete() {
        return correctRulesComplete;
    }
}
