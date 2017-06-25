package com.bob.main;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.BlockCoordinatesGenerator;

import java.util.HashMap;
import java.util.Map;

public class WriteModeLayer extends Group {

    Map<Block, Boolean> inputsMap = new HashMap<>();
    public WriteModeLayer(Skin skin) {
        Image inputsCreateBkg = new Image(TextureFactory.createTexture("screens/inputs_create.png"));
        inputsCreateBkg.setBounds(1400, 1035 - 700, 500, 700);
        addActor(inputsCreateBkg);

        BlockCoordinatesGenerator blockCoordGen = new BlockCoordinatesGenerator(1415, 1080 - 165);
        for(Block block : Block.values()) {
            int[] coord = blockCoordGen.getCoordinates(block.getType());
            createInput(skin, block, coord[0], coord[1]);
            inputsMap.put(block, false);
        }
    }

    private void createInput(Skin skin, final Block block, int refX, int refY) {
        Image inputImage = new Image(TextureFactory.createTexture("blocks/" + block.getImageName() + ".png"));
        final Image selected = new Image(TextureFactory.createTexture("blocks/selected.png"));
        inputImage.setBounds(refX, refY, 50, 50);
        selected.setBounds(refX - 2, refY - 2, 54, 54);
        selected.setVisible(false);
        inputImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inputsMap.put(block, !inputsMap.get(block));
                selected.setVisible(inputsMap.get(block));
            }
        });
        addActor(selected);
        addActor(inputImage);
        TextTooltip tooltip = new TextTooltip("  " + block.getTooltip() + "  ", skin, "tooltipStyle");
        tooltip.setInstant(true);
        inputImage.addListener(tooltip);
    }
}
