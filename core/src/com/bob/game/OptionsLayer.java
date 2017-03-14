package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Map;

public class OptionsLayer extends Layer {

    private Skin skin;
    private GameController gameController;
    public OptionsLayer(Skin skin, GameController gameController) {
        group.clear();
        this.skin = skin;
        this.gameController = gameController;
        initialVisibility = false;
        group.setVisible(initialVisibility);
    }

    protected void addOptionButtons(final Map<String, Boolean> options) {
        int optionsButtonY = 390;
        for (final String option: options.keySet()) {
            TextButton button = new TextButton(option, skin, "grey_button");
            button.setBounds(800, optionsButtonY, 500, 50);
            button.addListener(new ClickListener() {
                public void clicked(InputEvent ie, float x, float y) {
                    if (options.get(option)) {
                        correct();
                    } else {
                        incorrect();
                    }
                }
            });
            group.addActor(button);
            optionsButtonY -= 120;
        }

    }

    private void correct() {
        gameController.submit();
        group.clearChildren();
        TextButton button = new TextButton("Correct\n click for next level", skin, "green_button");
        button.setBounds(800, 250, 500, 150);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                group.clear();
                gameController.setDisableButton(true);
                gameController.loadNextLevel();
            }});
        group.addActor(button);
    }

    private void incorrect() {
        group.clearChildren();
        TextButton button = new TextButton("Incorrect, try again", skin, "red_button");
        button.setBounds(800, 250, 500, 50);
        button.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                group.clearChildren();
                addOptionButtons(gameController.getCurrentLevel().getOptions());

        }});
        group.addActor(button);
    }
}
