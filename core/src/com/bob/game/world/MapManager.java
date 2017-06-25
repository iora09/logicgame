package com.bob.game.world;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.bob.main.Config;
import com.bob.main.CreationMode;
import com.bob.main.TextureFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MapManager {
    private final TiledMap map;
    private IsometricTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private final TiledMapTileLayer floorLayer;
    private final TiledMapTileLayer objectsLayer;
    private Iterator waterIterator;
    private float elapsedSinceAnimation;

    public MapManager(int[][] floor, int[][] objects) {

        map = new TmxMapLoader().load("maps/default.tmx");

        Iterator<TiledMapTileSet> tileSets = map.getTileSets().iterator();
        while(tileSets.hasNext())
        {
            Iterator<TiledMapTile> tiles = tileSets.next().iterator();

            while(tiles.hasNext())
            {
                tiles.next().getTextureRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
        }

        floorLayer = (TiledMapTileLayer)map.getLayers().get("Floor");
        objectsLayer = (TiledMapTileLayer)map.getLayers().get("Objects");
        waterIterator = map.getTileSets().getTileSet("water").iterator();

        populateMap(floor, objects);

        elapsedSinceAnimation = 0;
    }

    private void populateMap(int[][] floor, int[][] objects) {

        for (int i = 0; i < floor.length; i++) {
            for (int j = 0; j < floor[i].length; j++) {
                floorLayer.getCell(i,j).setTile(map.getTileSets().getTile(floor[i][j]));

                if (objects[i][j] == 0) {
                    objectsLayer.getCell(i, j).getTile().setId(0);
                } else {
                    objectsLayer.getCell(i, j).setTile(map.getTileSets().getTile(objects[i][j]));
                }
            }
        }


    }

    public void initRender() {
        camera = new OrthographicCamera(1920, 1080);
        camera.position.set(Config.tileWidth * Config.noHorizontalTile - 50, 67, 0);

        renderer = new IsometricTiledMapRenderer(map);
    }

    public void draw(float deltaTime) {

        camera.update();

        renderer.setView(camera);
        renderer.render();

        elapsedSinceAnimation += deltaTime;

        if(elapsedSinceAnimation > 1/24f){
            updateWaterAnimations();
            elapsedSinceAnimation = 0;
        }
    }

    public String getType(int x, int y) {

        TiledMapTileLayer.Cell cell = floorLayer.getCell(x, y);//  Config.noVerticalTile + y);
        Object type = cell.getTile().getProperties().get("type");

        if (type != null) {
            return (String)type;
        } else {
            return "";
        }
    }

    private void updateWaterAnimations(){

        if (!waterIterator.hasNext()) {
            waterIterator = map.getTileSets().getTileSet("water").iterator();
        }

        TiledMapTile newTile = (TiledMapTile)waterIterator.next();

        for(int x = 0; x < floorLayer.getWidth();x++){
            for(int y = 0; y < floorLayer.getHeight();y++){

                TiledMapTileLayer.Cell cell = floorLayer.getCell(x,y);
                Object type = cell.getTile().getProperties().get("type");

                if (type != null){
                    if (type.equals("water")) cell.setTile(newTile);
                }
            }
        }
    }

    public String getLPSDescription() {

        StringBuilder sb = new StringBuilder();

        for(int x = 0; x < floorLayer.getWidth();x++) {
            for (int y = 0; y < floorLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = floorLayer.getCell(x,y);
                Object type = cell.getTile().getProperties().get("type");

                if (type != null){
                    sb.append(type + "(" + x + "," + y + ").");
                }
            }
        }

        return sb.toString();
    }

    public List<WorldCoordinates> getCoordinatesList(String layerName, String typeString) {
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(layerName);
        List<WorldCoordinates> res = new LinkedList<>();

        if (layer == null) {
            return res;
        }

        for(int x = 0; x < layer.getWidth();x++){
            for(int y = 0; y < layer.getHeight();y++){

                TiledMapTileLayer.Cell cell = layer.getCell(x,y);
                if (cell == null) continue;

                Object type = cell.getTile().getProperties().get("type");

                if (type != null){
                    if (type.equals(typeString)) {
                        res.add(new WorldCoordinates(x, y));
                    }
                }
            }
        }

        return res;
    }

    public String getLightsString() {
        List<WorldCoordinates> lights = getCoordinatesList("Objects", "light_bulb");
        StringBuilder sb = new StringBuilder();

        for (WorldCoordinates light: lights) {
            sb.append("lightBulb("+(int)light.getWorldX()+","+(int)light.getWorldY()+").");
        }

        return sb.toString();
    }

    public void setGold(int i, int j) {

        TiledMapTileSet tileSet = map.getTileSets().getTileSet("floor");

        TiledMapTile goldTile = tileSet.getTile(6);
        TiledMapTile questionTile = tileSet.getTile(7);

        for (TiledMapTile t : tileSet) {
            if (t.getProperties().get("type").equals("gold")) {
                goldTile = t;
            }

            if (t.getProperties().get("type").equals("question")) {
                questionTile = t;
            }
        }

        for(int x = 0; x < floorLayer.getWidth();x++){
            for(int y = 0; y < floorLayer.getHeight();y++){

                TiledMapTileLayer.Cell cell = floorLayer.getCell(x,y);
                Object type = cell.getTile().getProperties().get("type");

                if (type != null){
                    if (type.equals("gold")) {
                        cell.setTile(questionTile);
                    }
                }
            }
        }

        floorLayer.getCell(i, j).setTile(goldTile);
    }

    public boolean checkIfWet(WorldCoordinates coord) {
        String type = this.getType(Math.round(coord.getWorldX()), Math.round(coord.getWorldY()));

        return type.equals("water");
    }

    public boolean chekIfWon(WorldCoordinates coord, int noObjects) {
        String type = this.getType(Math.round(coord.getWorldX()), Math.round(coord.getWorldY()));

        return type.equals("gold") && noObjects == 0;
    }

    public boolean isQuestionMark(WorldCoordinates coord) {
        String type = this.getType(Math.round(coord.getWorldX()), Math.round(coord.getWorldY()));

        return type.equals("question");
    }

    public TiledMapTileLayer getFloorLayer() {
        return floorLayer;
    }

    public TiledMap getMap() {
        return map;
    }

    public TiledMapTileLayer getObjectsLayer() {
        return objectsLayer;
    }
}
