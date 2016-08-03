package com.bob.game.inputs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.bob.main.TextureFactory;

public class ModalLayer extends InputsLayer {

    private TextField textField;
    private int index;

    public ModalLayer(Skin skin) {
        super();
        this.skin = skin;
        this.index = -1;

        addActor(new Image(TextureFactory.createTexture("screens/macro_modal.png")));

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("impact_small");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new Image(TextureFactory.createTexture("buttons/cursor.png")).getDrawable();

        textField = new TextField("Macro Title", textFieldStyle);
        textField.setBounds(755, 1005, 415, 50);

        addActor(textField);
    }
    
    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
