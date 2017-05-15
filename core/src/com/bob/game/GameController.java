package com.bob.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.inputs.*;
import com.bob.game.levels.Level;
import com.bob.game.world.WorldController;

import java.util.*;

public class GameController {

    private final LayerGroup layerGroup;

    private Level currentLevel;

    private final InputsManager inputsManager;
    private final MacroManager macroManager;
    private final WorldController worldController;
    private int currentHint = 0;
    private int numberOfSubmits = 0;

    public GameController(Skin skin, OrthographicCamera camera) {

        layerGroup = new LayerGroup();

        layerGroup.add("background", new BackgroundLayer(skin));
        layerGroup.add("controls", new ControlsLayer(skin, this));
        layerGroup.add("inputs", new InputsLayer(skin));
        layerGroup.add("macro", new MacroLayer(skin));
        layerGroup.add("modal_inputs", new ModalLayer(skin));
        layerGroup.add("winning", new WinningLayer(skin, this));
        layerGroup.add("message", new MessageLayer(skin, this));
        layerGroup.add("help screen", new HelpScreen(skin));

        inputsManager = new InputsManager();
        macroManager = new MacroManager();
        worldController = new WorldController();

        worldController.setCamera(camera);

        inputsManager.setLayer((InputsLayer)layerGroup.get("inputs"));
        inputsManager.initRuleView(skin, 1475, 1080 - 495);

        macroManager.setLayers((MacroLayer)layerGroup.get("macro"), (ModalLayer)layerGroup.get("modal_inputs"));
        macroManager.initView(skin);
        macroManager.addButtons(skin);
    }

    public void reset() {
        if (currentLevel.allowRuleReset()) {
            inputsManager.resetRules();
            macroManager.resetMacros();
        }
        resetWorld();
    }

    public void startNewLevel() {

        currentHint = 0;
        numberOfSubmits = 0;

        if (currentLevel.hasHints()) {

        }

        if (currentLevel.hasTutorial()) {
            ((HelpScreen)layerGroup.get("help screen")).setImages(currentLevel.getTutorialImages());
            layerGroup.setVisibility("help screen", true);
        }

        if (currentLevel.allowMacro()) {
            layerGroup.setVisibility("macro", true);
            layerGroup.setVisibility("inputs", false);

            macroManager.resetMacros();
            macroManager.resetMacroInputs();

        } else {
            layerGroup.setVisibility("macro", false);
            layerGroup.setVisibility("inputs", true);

            inputsManager.setupRules(currentLevel.getNoRules(), currentLevel.getRules(), false);
            inputsManager.setupInputs(currentLevel.getInputs(), 1415, 1080 - 165);
        }

        ((ControlsLayer)layerGroup.get("controls")).disableReset(currentLevel.allowRuleReset());
        ((ControlsLayer)layerGroup.get("controls")).disableHints(currentLevel.hasHints());

        worldController.setupWorld(currentLevel);
        worldController.initRender();
        ((BackgroundLayer)layerGroup.get("background")).changeText(currentLevel.getText());
        ((BackgroundLayer)layerGroup.get("background")).setMaxLights(worldController.getMaxObjects());
    }

    public void render(float deltaTime) {

        ((ControlsLayer)layerGroup.get("controls")).disableSubmit(!inputsManager.checkRules());

        inputsManager.toggleLights();
        inputsManager.lightOffRules();
        inputsManager.lightOnRule(worldController.getCurrentRuleIndexes());

        macroManager.toggleLights();

        worldController.render(deltaTime);
        ((BackgroundLayer)layerGroup.get("background")).setNoLights(worldController.getMaxObjects() - worldController.getNoObjects());
        if (worldController.isBobWet()) {
            ((MessageLayer) layerGroup.get("message")).changeText("Uh oh, the rule highlighted made Bob fall in the water. Can you fix it?");
            ((MessageLayer) layerGroup.get("message")).addListenerToNextButton(new ClickListener() {
                public void clicked(InputEvent ie, float x, float y) {
                    worldController.updateCurrentRule();
                }
            });
            layerGroup.setVisibility("message", true);
        }
        if (worldController.isLevelWon()) {
            currentLevel.save();
            layerGroup.setVisibility("winning", true);
        } else if (worldController.isBobConfused()) {
            ((MessageLayer) layerGroup.get("message")).changeText("Uh oh, Bob does not know which rule to follow...");
            layerGroup.setVisibility("message", true);
        } else if (currentLevel.getType().equals("WRITE") && numberOfSubmits > 1
                && !getTellemetricHints(worldController, inputsManager).isEmpty()){
            ((MessageLayer) layerGroup.get("message")).changeText(getTellemetricHints(worldController, inputsManager));
            layerGroup.setVisibility("message", true);
            numberOfSubmits = 0;
        }

    }

    public void setLevel(Level level) {
        this.currentLevel = level;
    }

    public void loadNextLevel() {
        numberOfSubmits = 0;
        if (currentLevel.getNext() == null) { // Mode completed, back to menu
            hide();
        } else {
            currentLevel = currentLevel.getNext();
            startNewLevel();
        }

        layerGroup.setVisibility("winning", false);
    }

    public void submit() {
        resetWorld();
        numberOfSubmits++;

        if (currentLevel.allowMacro()) {
            startLPSAnim(macroManager.getRulesString());
        } else {
            if (inputsManager.mixedParadigmUsed()) {
                ((HelpScreen)layerGroup.get("help screen")).setImage("screens/both_paradigm.png");
                layerGroup.setVisibility("help screen", true);
            } else if (inputsManager.onlyConsequentUsed()) {
                startMockAnim(inputsManager.getBlockStack());
            } else {
                startLPSAnim(inputsManager.getRulesString());
            }
        }
    }

    public void resetWorld() {
        worldController.resetStage(currentLevel.getX(), currentLevel.getY());
        ((BackgroundLayer)layerGroup.get("background")).setNoLights(0);
    }

    private void startMockAnim(LinkedList<Block> blockStack) {
        worldController.startMockAnimation(blockStack);
    }

    private void startLPSAnim(String LPS) {
        worldController.startLPSAnimation(currentLevel, LPS);
    }

    public void updateSpeed(float value) {
        worldController.updateSpeed(value);
    }

    public void linkStage(Stage stage) {
        layerGroup.setStage(stage);
        worldController.setStage(stage);
    }

    public void show() {
        layerGroup.show();
    }

    public void hide() {
        layerGroup.hide();
    }

    public boolean isVisible() {
        return layerGroup.isVisible();
    }

    public void displayHints() {
        if (currentLevel.hasHints()) {
            ((MessageLayer) layerGroup.get("message")).changeText(currentLevel.getHints()[currentHint]);
            layerGroup.setVisibility("message", true);
            currentHint = (currentHint + 1) % currentLevel.getHints().length;
        }
    }

    private String getTellemetricHints(WorldController worldController, InputsManager inputsManager) {
        Set<String> tiles = new HashSet<>();
        TiledMapTileLayer floor = worldController.getMapManager().getFloorLayer();
        for (int i = 0 ; i < floor.getWidth(); ++i) {
            for (int j = 0 ; j < floor.getHeight(); ++j) {
                if (!worldController.getMapManager().getType(i,j).equals("water")
                        && !worldController.getMapManager().getType(i,j).equals("gold")) {
                   tiles.add(worldController.getMapManager().getType(i,j));
                }
            }
        }

        Set<String> rules = new HashSet<>();
        for (int i = 0; i < inputsManager.getRules().length; ++i) {
            for (int j = 0; j <  inputsManager.getRules()[i].length; ++j) {
                if(inputsManager.getRules()[i][j].getType() == Type.FLUENT) {
                    rules.add(inputsManager.getRules()[i][j].getImageName());
                }
            }
        }

        tiles.removeAll(rules);

        StringBuilder hintMessageBuilder = new StringBuilder("Hint : There are more coloured tiles on the map, but you " +
                "are not using the following in your rules: ");

        for (String tileNotUsed : tiles) {
            hintMessageBuilder.append(tileNotUsed + ";");
        }

        if (tiles.size() == 0) {
            numberOfSubmits = 0;
            return "";
        } else {
            return hintMessageBuilder.toString();
        }
    }

    public void displayHelp() {
        int n = currentLevel.hasTutorial() ? currentLevel.getTutorialImages().length : 0;
        String[] res = new String[n + 1];

        System.arraycopy(currentLevel.getTutorialImages(), 0, res, 0, n);
        res[n] = "screens/help.png";

        ((HelpScreen)layerGroup.get("help screen")).setImages(res);
        layerGroup.setVisibility("help screen", true);
    }
    public void resetTellemetricHints() {
        numberOfSubmits = 0;
    }
}
