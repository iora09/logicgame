package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

class WinningLayer extends Layer {

    TextButton nextButton;
    TextButton backEdit;
    TextButton stay;

    public WinningLayer(final Skin skin, final GameController controller) {
        initialVisibility = false;

        group.addActor(new Image(TextureFactory.createTexture("screens/winning.png")));

        nextButton = new TextButton("NEXT LEVEL", skin, "big_grey_button");
        nextButton.setBounds(760, 380, 400, 100);
        nextButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                controller.loadNextLevel(skin);
            }
        });

        backEdit = new TextButton("BACK TO EDIT", skin, "big_grey_button");
        backEdit.setBounds(550, 380, 500, 100);
        backEdit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.goBackToCreate(skin);
            }
        });
        backEdit.setVisible(false);

        stay = new TextButton("STAY ON THIS LEVEL", skin, "big_grey_button");
        stay.setBounds(1070, 380, 530, 100);
        stay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.reset();
                controller.goBackToPlay();
            }
        });
        stay.setVisible(false);

        group.addActor(nextButton);
        group.addActor(backEdit);
        group.addActor(stay);
    }

    public void changeWinningButtons(boolean isPreview) {
        nextButton.setVisible(!isPreview);
        backEdit.setVisible(isPreview);
        stay.setVisible(isPreview);
    }


}
