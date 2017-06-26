package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class PreviewLayer extends Layer {
    public PreviewLayer(final Skin skin, final GameController controller) {
        initialVisibility = false;

        group.addActor(new Image(TextureFactory.createTexture("screens/preview.png")));

        TextButton okButton = new TextButton("OK", skin, "big_grey_button");
        okButton.setBounds(360, 380, 400, 100);
        okButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                controller.goBackToCreate(skin);
            }
        });

        TextButton cancelButton = new TextButton("Cancel", skin, "big_grey_button");
        cancelButton.setBounds(860, 380, 400, 100);
        cancelButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                controller.goBackToPlay();
            }
        });


        group.addActor(okButton);
        group.addActor(cancelButton);
    }
}
