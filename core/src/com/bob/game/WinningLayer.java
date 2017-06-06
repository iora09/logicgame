package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

class WinningLayer extends Layer {

    public WinningLayer(final Skin skin, final GameController controller) {
        initialVisibility = false;

        group.addActor(new Image(TextureFactory.createTexture("screens/winning.png")));

        TextButton nextButton = new TextButton("NEXT LEVEL", skin, "big_grey_button");
        nextButton.setBounds(760, 380, 400, 100);
        nextButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                controller.loadNextLevel(skin);
            }
        });

        group.addActor(nextButton);
    }

}
