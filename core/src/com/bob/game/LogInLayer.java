package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.GameStore;
import com.bob.main.TextureFactory;

public class LogInLayer extends Layer {

    public LogInLayer(final Skin skin, final GameStore gameStore) {
        initialVisibility = false;
        setInitialVisibility();

        Image logInPanel = new Image(TextureFactory.createTexture("screens/modal.png"));
        logInPanel.setBounds(640, 400, 600, 380);
        addActor(logInPanel);

        Label usernameLabel = new Label("Username", skin, "label_style");
        Label passwordLabel = new Label("Password", skin.get("label_style", Label.LabelStyle.class));

        usernameLabel.setBounds(660, 650, 200, 50);
        passwordLabel.setBounds(660, 550, 200, 50);

        final TextField usernameField = new TextField("", skin);
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        usernameField.setBounds(900, 650, 300, 50);
        passwordField.setBounds(900, 550, 300, 50);

        TextButton logInButton = new TextButton("Log in", skin, "grey_button");
        logInButton.setBounds(840, 450, 200, 60);
        logInButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ie, float x, float y) {
                gameStore.tryLoggingIn(group, usernameField.getText(), passwordField.getText(), skin, false);
            }
        });

        addActor(usernameLabel);
        addActor(passwordLabel);
        addActor(usernameField);
        addActor(passwordField);
        addActor(logInButton);
    }
}
