package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TryAgainLayer extends Layer{

    public TryAgainLayer(final Skin skin, final GameController gameController) {
        initialVisibility = false;

        TextButton tryAgainButton = new TextButton("TRY AGAIN", skin, "green_button");
        tryAgainButton.setBounds(1700, 10, 200, 60);
        tryAgainButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                gameController.tryAgain(skin);
                setVisibility(false);
            }
        });

        group.addActor(tryAgainButton);
    }
}
