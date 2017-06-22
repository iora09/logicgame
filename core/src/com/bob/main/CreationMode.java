package com.bob.main;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.Layer;
import com.bob.game.inputs.Tile;


public class CreationMode {

    private Image selectedTileImage = new Image(TextureFactory.createTexture("maps/selected_tile.png"));
    private Image bobSelectedImage = new Image(TextureFactory.createTexture("bob/bob_selected.png"));
    private Layer layer;
    public static Tile selected = null;
    public static boolean bobSelected = false;
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

        //  ADD the Question Mark only if in Read Mode
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

        // WATER
        final Tile inputTile = Tile.getTile(9);
        Image inputTileImage = new Image(TextureFactory.createTexture(inputTile.getImagePath()));
        inputTileImage.setBounds(x , finalY, 100,80);
        final int finalX = x;
        inputTileImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = inputTile;
                bobSelected = false;
                setSelectedImage(finalX, finalY);
            }
        });
        layer.addActor(inputTileImage);

        x = x + 130;

        // BOB
        Image bobImage = new Image(TextureFactory.createTexture("bob/bob.png"));
        bobImage.setBounds(x, finalY, 80, 130);
        final int finalXX = x;
        bobImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = null;
                bobSelected = true;
                bobSelectedImage.setBounds(finalXX, finalY, 85, 135);
                layer.addActorFirst(bobSelectedImage);
                layer.removeActor(selectedTileImage);
            }
        });

        layer.addActor(bobImage);
    }

    private void setSelectedImage(int x, int y) {
        layer.removeActor(selectedTileImage);
        layer.removeActor(bobSelectedImage);
        selectedTileImage.setBounds(x, y, 105,85);
        layer.addActorFirst(selectedTileImage);
    }

}
