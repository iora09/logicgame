package com.bob.main;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.Layer;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.BlockCoordinatesGenerator;
import com.bob.game.inputs.InputsLayer;
import com.bob.game.inputs.Rule;
import com.bob.game.inputs.RuleCell;

public class ReadModeLayer extends Group {
    public static RuleCell selectedRuleCell = null;
    private Rule[] rules = new Rule[8];
    private static Layer ruleLayer = new InputsLayer();
    public static Layer inputLayer = new InputsLayer();

    public ReadModeLayer(Skin skin) {
        Image inputsCreateBkg = new Image(TextureFactory.createTexture("screens/rules_create.png"));
        inputsCreateBkg.setBounds(1400, 1035 - 650, 500, 650);
        addActor(inputsCreateBkg);
        int y = 1035 - 117;
        for (int i = 0; i < 8; ++i) {
            rules[i] = new Rule();
            rules[i].initViewForCreation(ruleLayer, skin, 1480, y);
            y -= 70;
        }
        createInputsPanel(skin);
        inputLayer.setVisibility(false);
        ruleLayer.setVisibility(true);
    }

    public static void createInputsPanel(Skin skin) {
        Image panel = new Image(TextureFactory.createTexture("screens/modal.png"));
        panel.setBounds(750, 1080 - 900, 500, 700);
        inputLayer.addActor(panel);
        BlockCoordinatesGenerator blockCoordGen = new BlockCoordinatesGenerator(764, 1080 - 270);
        for(Block block : Block.values()) {
            int[] coord = blockCoordGen.getCoordinates(block.getType());
            createInput(skin, block, coord[0], coord[1]);
        }

        TextButton OK = new TextButton("OK", skin, "grey_button");
        OK.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                     inputLayer.setVisibility(false);
            }
        });

        TextButton cancel = new TextButton("Cancel", skin, "grey_button");
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedRuleCell != null) {
                    selectedRuleCell.reset();
                }
                inputLayer.setVisibility(false);
            }
        });
        OK.setBounds(850, 1080 - 880, 100, 50);
        cancel.setBounds(970, 1080-880, 150, 50);
        inputLayer.addActor(OK);
        inputLayer.addActor(cancel);
    }

    private static void createInput(Skin skin, final Block block, final int refX, final int refY) {
        Image inputImage = new Image(TextureFactory.createTexture("blocks/" + block.getImageName() + ".png"));
        inputImage.setBounds(refX, refY, 50, 50);
        inputImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedRuleCell.reset();
                selectedRuleCell.setPayload(block);
                selectedRuleCell.setImage(false);
            }
        });
        inputLayer.addActor(inputImage);
        TextTooltip tooltip = new TextTooltip("  " + block.getTooltip() + "  ", skin, "tooltipStyle");
        tooltip.setInstant(true);
        inputImage.addListener(tooltip);

    }

    public void setStage(Stage stage) {
        ruleLayer.setStage(stage);
        inputLayer.setStage(stage);
    }

    @Override
    public void setVisible(boolean visible) {
        ruleLayer.setVisibility(visible);
        if(!visible) {
            inputLayer.setVisibility(visible);
        }
    }
}
