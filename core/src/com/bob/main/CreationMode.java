package com.bob.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.Layer;
import com.bob.game.inputs.Tile;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.bob.game.world.MapManager;

import java.io.File;

public class CreationMode {

    private Image selectedTileImage = new Image(TextureFactory.createTexture("maps/selected_tile.png"));

    private Layer layer;
    public static Tile selected = null;
    //WriteModeLayer writeModeLayer = new WriteModeLayer();
    //ReadModeLayer readModeLayer = new ReadModeLayer();

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public Layer getLayer() {
        return layer;
    }

    public void createInputTiles(Skin skin, String mode) {
        int x = 25;
        final int finalY = 1080 - 130;
        for (int i = 1; i < 8; ++i) {
            final Tile inputTile = Tile.getTile(i);
            Image inputTileImage = new Image(TextureFactory.createTexture(inputTile.getImagePath()));
            inputTileImage.setBounds(x , finalY, 100,80);
            final int finalX = x;
            inputTileImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selected = inputTile;
                    setSelectedImage(finalX, finalY);
                }
            });
            layer.addActor(inputTileImage);
            x = x + 110;
        }

        if (mode == "read") {
            final Tile inputTile = Tile.getTile(8);
            Image inputTileImage = new Image(TextureFactory.createTexture(inputTile.getImagePath()));
            inputTileImage.setBounds(x , finalY, 100,80);
            final int finalX = x;
            inputTileImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selected = inputTile;
                    setSelectedImage(finalX, finalY);
                }
            });
            x = x + 110;
            layer.addActor(inputTileImage);
        }
        final Tile inputTile = Tile.getTile(9);
        Image inputTileImage = new Image(TextureFactory.createTexture(inputTile.getImagePath()));
        inputTileImage.setBounds(x , finalY, 100,80);
        final int finalX = x;
        inputTileImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = inputTile;
                setSelectedImage(finalX, finalY);
            }
        });
        layer.addActor(inputTileImage);
    }

    private void setSelectedImage(int x, int y) {
        layer.removeActor(selectedTileImage);
        selectedTileImage.setBounds(x, y, 105,85);
        layer.addActorFirst(selectedTileImage);
    }

}
