package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class SetNameLayer extends Layer {

    public SetNameLayer(final Skin skin, final GameController controller) {
        initialVisibility = false;
        group.setVisible(initialVisibility);

        Image modal = new Image(TextureFactory.createTexture("screens/modal.png"));
        modal.setBounds(710, 540, 500, 300);
        group.addActor(modal);

        Label infoLabel = new Label("Name your game: ", skin, "label_style");
        infoLabel.setBounds(760, 780, 150, 40);
        group.addActor(infoLabel);

        final TextField nameField = new TextField(null, skin);
        nameField.setBounds(800, 660, 300, 50);
        group.addActor(nameField);

        TextButton upload = new TextButton("Upload", skin, "grey_button");
        upload.setBounds(900, 560, 150, 60);
        upload.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               controller.uploadCurrentLevel(skin, nameField.getText());
            }
        });
        group.addActor(upload);
    }
}
