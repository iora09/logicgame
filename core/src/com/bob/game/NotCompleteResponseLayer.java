package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class NotCompleteResponseLayer extends Layer {

    public NotCompleteResponseLayer(final Skin skin) {
        initialVisibility = false;
        Image notComplete = new Image(TextureFactory.createTexture("screens/not_complete.png"));
        notComplete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                group.setVisible(false);
            }
        });
        group.addActor(notComplete);

        TextButton okButton = new TextButton("OK", skin, "green_button");
        okButton.setBounds(1700, 10, 200, 60);
        okButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                setVisibility(false);
            }
        });

        group.addActor(okButton);
    }
}
