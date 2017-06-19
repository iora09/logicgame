package com.bob.game.inputs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.bob.game.Layer;
import com.bob.main.TextureFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InputsLayer extends Layer {

    public static Block selected = null;
    protected final List<DragAndDrop.Target> targets;
    private final List<Draggable> draggables;
    private Image selectedImage;
    protected Skin skin;

    public InputsLayer(){
        initialVisibility = false;
        targets = new ArrayList<>();
        draggables = new ArrayList<>();
    }

    public InputsLayer(Skin skin) {
        this();
        this.skin = skin;

        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        skin.add("default", new Label.LabelStyle(font, Color.WHITE));

        // Inputs
        for (Block b: Block.values()) {
            skin.add(b.getImageName(), TextureFactory.createTexture("blocks/"+ b.getImageName() +".png"));
        }
        skin.add("macro_block", TextureFactory.createTexture("blocks/macro.png"));

        // Rules
        skin.add("red_light", TextureFactory.createTexture("lights/red.png"));
        skin.add("green_light", TextureFactory.createTexture("lights/green.png"));
        skin.add("target", TextureFactory.createTexture("blocks/target.png"));

        selectedImage = new Image(skin, "selected");
    }

    public List<DragAndDrop.Target> getTargets() {
        return targets;
    }

    public void addTargets(DragAndDrop.Target[] targets) {
        Collections.addAll(this.targets, targets);
    }

    public void addTarget(DragAndDrop.Target target) {
        this.targets.add(target);
    }

    public void createInput(final Block block, final int refX, final int refY) {

        Image dragImage = new Image(skin, block.getImageName());
        dragImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = block;
                removeSelected();
                addSelected(refX, refY);
            }
        });
        Image draggingImage = new Image(skin, block.getImageName());
        dragImage.setBounds(refX, refY, 50, 50);

        Draggable draggable = new Draggable(this, dragImage, draggingImage, block);
        draggable.setTooltip(skin, block.getTooltip());

        for (DragAndDrop.Target t: targets) {
            draggable.addTarget(t);
        }
        draggables.add(draggable);
    }

    public void clearInputs() {
        for (Draggable i: draggables) {
            i.clear();
        }
        draggables.clear();
    }

    public Skin getSkin() {
        return skin;
    }

    private void removeSelected() {
        if(selectedImage != null) {
            this.removeActor(selectedImage);
        }
    }

    private void addSelected(int refX, int refY) {
        if (selectedImage != null) {
            selectedImage.setBounds(refX - 3, refY - 3, 55, 57);
            this.addActorFirst(selectedImage);
        }
    }
}