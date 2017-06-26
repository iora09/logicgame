package com.bob.main;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.Layer;
import com.bob.game.inputs.Tile;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.bob.game.world.WorldController;


public class CreationMode {

    private Image selectedTileImage = new Image(TextureFactory.createTexture("maps/selected_tile.png"));
    private Image bobSelectedImage = new Image(TextureFactory.createTexture("bob/bob_selected.png"));
    private Layer layer;
    public static Tile selected = null;
    public static boolean bobSelected = false;
    public boolean lightbulbSelected = false;

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public Layer getLayer() {
        return layer;
    }

    public void createInputTiles(String mode) {
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
        if (mode.equals("read")) {
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
                lightbulbSelected = false;
                setSelectedImage(finalX, finalY);
            }
        });
        layer.addActor(inputTileImage);

        x = x + 140;

        // BOB
        final Image bobImage = new Image(TextureFactory.createTexture("bob/bob.png"));
        bobImage.setBounds(x, finalY - 5, 60, 120);
        final int finalXX = x;
        bobImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected = null;
                bobSelected = true;
                lightbulbSelected = false;
                bobSelectedImage.setBounds(finalXX, finalY - 5, 62, 122);
                layer.addActorFirst(bobSelectedImage);
                layer.removeActor(selectedTileImage);
            }
        });
        x = x + 90;
        layer.addActor(bobImage);

        if (mode.equals("macro")) {
            // LIGHTBULB
            final Image lightbulbImage = new Image(TextureFactory.createTexture("macro/light_bulb.png"));
            lightbulbImage.setBounds(x, finalY, 50, 100);
            lightbulbImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selected = null;
                    bobSelected = false;
                    lightbulbSelected = true;
                    layer.removeActor(bobSelectedImage);
                    layer.removeActor(selectedTileImage);
                }
            });
            layer.addActor(lightbulbImage);
        }
    }

    private void setSelectedImage(int x, int y) {
        layer.removeActor(selectedTileImage);
        layer.removeActor(bobSelectedImage);
        selectedTileImage.setBounds(x, y, 105,85);
        layer.addActorFirst(selectedTileImage);
    }

    public void addGroup(Group group) {
        layer.addActor(group);
        layer.setCreationGroup(group);
    }

    public Level getCreatedLevel(WorldController worldController) {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (layer.getCreationGroup() instanceof WriteModeLayer) {
           sb.append("<root type=\"WRITE\">\n");
           sb.append(((WriteModeLayer)layer.getCreationGroup()).getXML());
        } else if (layer.getCreationGroup() instanceof ReadModeLayer) {
            sb.append("<root type=\"READ\">\n");
            sb.append(((ReadModeLayer)layer.getCreationGroup()).getXML());
        } else {
            sb.append("<root type=\"MACRO\">\n");
        }
        sb.append(worldController.getXML());
        sb.append("</root>");
        System.out.println(sb.toString());
        Level createdLevel = LevelFactory.createLevel(sb.toString(), "");
        return createdLevel;
    }
}
