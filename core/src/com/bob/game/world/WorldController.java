package com.bob.game.world;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.bob.game.GameController;
import com.bob.game.inputs.Block;
import com.bob.game.levels.Level;
import com.bob.main.CreationMode;
import com.bob.main.ReadModeLayer;
import com.bob.main.TextureFactory;

import java.util.*;

public class WorldController {

    //view
    private boolean isAnimPlaying;
    private float speedFactor;
    private int nbWon;

    // Bob
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final Entity bob;

    // Objects
    private List<Entity> objects;

    // Map and LPS
    private MapManager mapManager;
    private InstructionStrategy instructionRetriever;
    private List<Integer> currentRuleIndexes;

    // Golden cells
    private Stage stage;
    private final List<ClickListener> goldListener;

    private final List<ClickListener> createModeListeners;

    //LightBulbs for creation mode
    Map<WorldCoordinates, Image> lightBulbs = new HashMap<>();

    public WorldController() {
        goldListener = new LinkedList<>();
        isAnimPlaying = false;
        speedFactor = 2f;
        nbWon = 0;
        currentRuleIndexes = new ArrayList<Integer>();
        bob = new Entity(0, 0);
        objects = new ArrayList<Entity>();
        createModeListeners = new ArrayList<>();
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void setupWorld(Level level) {
        mapManager = new MapManager(level.getFloor(), level.getObjects());
        resetStage(level.getX(), level.getY());
    }

    public void initRender() {
        mapManager.initRender();
        setGoldListeners();
        batch = new SpriteBatch();
    }

    private void setGoldListeners() {
        clearListener();
        List<WorldCoordinates> questions = mapManager.getCoordinatesList("Floor", "question");

        for (WorldCoordinates coord: questions) {
            addQuestionListener(coord);
        }

    }

    private void clearListener() {
        for (ClickListener listener : goldListener) {
            stage.removeListener(listener);
        }
    }

    public void resetLights() {
        List<WorldCoordinates> lights = mapManager.getCoordinatesList("Objects", "light_bulb");
        objects = new ArrayList<>();

        for (WorldCoordinates l: lights) {
            Entity light = new Entity(l.getWorldX(), l.getWorldY());
            light.updateState(EntityState.LIGHT);

            objects.add(light);
        }

    }

    public void resetBob(float x, float y) {
        nbWon = 0;
        bob.setPosition(x, y);
        isAnimPlaying = false;
    }

    public void resetStage(float x, float y) {
        resetBob(x, y);
        resetLights();
        currentRuleIndexes.clear();
        for (ClickListener listener : createModeListeners) {
            stage.removeListener(listener);
        }
    }

    public void startLPSAnimation(Level level, String rules) {
        instructionRetriever = new LPSHandler(mapManager.getLPSDescription(), mapManager.getLightsString(), rules, level.getX(), level.getY());
        isAnimPlaying = true;
    }

    public void startMockAnimation(LinkedList<Block> blockStack) {
        instructionRetriever = new MockLPSHandler(blockStack);
        isAnimPlaying = true;
    }

    public void render(float deltaTime) {
        float deltaTimeAdjusted = isAnimPlaying ? deltaTime * speedFactor: 0;

        // UPDATE OF ENTITY
        updateWorld(deltaTimeAdjusted);

        //Map
        if (mapManager != null) mapManager.draw(deltaTimeAdjusted);

        // Batch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (bob != null) bob.draw(batch);


        for (Entity object: objects) {
            object.increaseTime(deltaTimeAdjusted);
            object.draw(batch);
        }

        batch.end();
    }

    public void updateWorld(float deltaTimeAdjusted) {
        updateBob(deltaTimeAdjusted);
        cleanObjects();
    }

    private void cleanObjects() {
        List<Entity> toDelete = new LinkedList<>();
        for (Entity object: objects) {
            if (object.getCoord().collide(bob.getCoord())) {
                toDelete.add(object); // todo anim
            }
        }

        for (Entity o: toDelete) {
            objects.remove(o);
        }
    }

    public void updateBob(float deltaTime) {
        bob.increaseTime(deltaTime);

        if (bob.needInstructions() && isAnimPlaying) {
            retrieveInstructions();
            updateGameState();
        }
    }

    private void retrieveInstructions() {
        instructionRetriever.update();
        bob.updateState(instructionRetriever.getState());
    }

    private void updateGameState() {

        WorldCoordinates coord = bob.getCoord();

        if (mapManager.checkIfWet(coord)) {
            bob.updateState(EntityState.WET);
            isAnimPlaying = false;
        }

        // Adds delay to show winning screen
        if (mapManager.chekIfWon(coord, objects.size())) {
            bob.updateState(EntityState.WON);
            nbWon++;
        }

        // Update current rule
        currentRuleIndexes.clear();
        currentRuleIndexes.addAll(instructionRetriever.getAppliedRuleIndexes());
    }

    public boolean isLevelWon() {
        return nbWon > 0;
    }

    public void updateSpeed(float newValue) {
        speedFactor = newValue;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void addQuestionListener(final WorldCoordinates coord) {

        ClickListener listener = new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Math.abs(x - coord.getScreenX()) < 50 && Math.abs(y - coord.getScreenY()) < 30) {
                    if (!isAnimPlaying) {
                        mapManager.setGold((int) coord.getWorldX(), (int) coord.getWorldY());
                    }
                }
            }
        };

        goldListener.add(listener);
        stage.addListener(listener);
    }

    public List<Integer> getCurrentRuleIndexes() {
        return currentRuleIndexes;
    }

    public int getNoObjects() {
        return objects.size();
    }

    public int getMaxObjects() {
        return mapManager.getCoordinatesList("Objects", "light_bulb").size();
    }

    public boolean isBobConfused() {
        return bob.isConfused() && isAnimPlaying;
    }

    public boolean isOnQuestionMark() {
        return mapManager.isQuestionMark(bob.getCoord());
    }

    public void setLevelWon() {
        nbWon ++;
    }

    public void addClickListenersToMap(final CreationMode creationMode, final GameController gm) {
        final TiledMapTileLayer floorLayer = mapManager.getFloorLayer();
        final TiledMapTileLayer objectLayer = mapManager.getObjectsLayer();
        final TiledMap map = mapManager.getMap();
        for(Image lightBulb : lightBulbs.values()) {
            creationMode.getLayer().removeActor(lightBulb);
        }
        lightBulbs.clear();
        for (int x = 0; x < floorLayer.getWidth(); x++) {
            for (int y = 0; y < floorLayer.getHeight(); y++) {
                final WorldCoordinates coord = new WorldCoordinates(x, y);
                ClickListener listener = new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(!ReadModeLayer.inputLayer.isVisible()
                                && !gm.isLayerVisible("setName")
                                && !gm.isLayerVisible("logIn")) {
                            if (coord.isInMap() && Math.abs(x - coord.getScreenX()) < 40 && Math.abs(y - coord.getScreenY()) < 20)
                                if (creationMode.selected != null) {
                                    floorLayer.getCell((int) coord.getWorldX(), (int) coord.getWorldY()).setTile(map.getTileSets().getTile(creationMode.selected.getXmlNumber()));
                                } else if (creationMode.bobSelected) {
                                    resetBob((int) coord.getWorldX(), (int) coord.getWorldY());
                                } else if (creationMode.lightbulbSelected) {
                                    if (lightBulbs.containsKey(coord) || objectLayer.getCell((int) coord.getWorldX(), (int) coord.getWorldY()).getTile().getId() == 25) {
                                        objectLayer.getCell((int) coord.getWorldX(), (int) coord.getWorldY()).getTile().setId(0);
                                        creationMode.getLayer().removeActor(lightBulbs.get(coord));
                                        lightBulbs.remove(coord);
                                    } else {
                                        objectLayer.getCell((int) coord.getWorldX(), (int) coord.getWorldY()).setTile(map.getTileSets().getTile(25));
                                        Image lightBulb = new Image(TextureFactory.createTexture("macro/light_bulb.png"));
                                        lightBulb.setBounds((int) coord.getScreenX() - 10, (int) coord.getScreenY(), 40, 60);
                                        creationMode.getLayer().addActor(lightBulb);
                                        lightBulbs.put(coord, lightBulb);
                                    }
                                }
                        }
                    }
                };
                stage.addListener(listener);
                createModeListeners.add(listener);
            }
        }
    }

    public String getXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<bob x=\""+ (int)bob.getCoord().getWorldX() + "\" y=\""+ (int)bob.getCoord().getWorldY() + "\" />\n");
        sb.append(mapManager.getXML());
        return sb.toString();
    }
}
