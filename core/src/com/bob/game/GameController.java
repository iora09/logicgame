package com.bob.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.inputs.*;
import com.bob.game.levels.Level;
import com.bob.game.world.WorldController;
import com.bob.main.*;

import java.util.LinkedList;

public class GameController {

    private final LayerGroup layerGroup;

    private Level currentLevel;

    private final InputsManager inputsManager;
    private final MacroManager macroManager;
    private final WorldController worldController;
    private final ChoicesManager choicesManager;
    private final CreationMode creationMode;
    private final GameStore gameStore;
    private int currentHint = 0;
    private boolean isInPreview = false;

    public GameController(Skin skin, OrthographicCamera camera, GameStore gameStore) {

        layerGroup = new LayerGroup();

        layerGroup.add("background", new BackgroundLayer(skin));
        layerGroup.add("controls", new ControlsLayer(skin, this));
        layerGroup.add("inputs", new InputsLayer(skin));
        layerGroup.add("macro", new MacroLayer(skin));
        layerGroup.add("modal_inputs", new ModalLayer(skin));
        layerGroup.add("winning", new WinningLayer(skin, this));
        layerGroup.add("message", new MessageLayer(skin, this));
        layerGroup.add("help screen", new HelpScreen(skin));
        layerGroup.add("tutorial", new InputsLayer(skin));
        layerGroup.add("tryAgain", new TryAgainLayer(skin, this));
        layerGroup.add("notComplete", new NotCompleteResponseLayer(skin));
        layerGroup.add("create", new CreateLayer());
        layerGroup.add("setName", new SetNameLayer(skin, this));
        layerGroup.add("logIn", new LogInLayer(skin, gameStore));

        inputsManager = new InputsManager();
        macroManager = new MacroManager();
        worldController = new WorldController();
        choicesManager = new ChoicesManager();
        creationMode = new CreationMode();
        worldController.setCamera(camera);
        this.gameStore = gameStore;

        inputsManager.setLayer((InputsLayer)layerGroup.get("inputs"));
        inputsManager.initRuleView(skin, 1475, 1080 - 495, true);

        macroManager.setLayers((MacroLayer)layerGroup.get("macro"), (ModalLayer)layerGroup.get("modal_inputs"));
        macroManager.initView(skin);
        macroManager.addButtons(skin);

        choicesManager.setLayer(((InputsLayer)layerGroup.get("tutorial")));
        choicesManager.initRuleView(skin, 1475, 1080-495, false);

        creationMode.setLayer((CreateLayer) layerGroup.get("create"));
        layerGroup.get("logIn").setVisibility(false);
    }

    public void reset() {
        if (currentLevel.allowRuleReset()) {
            inputsManager.resetRules();
            macroManager.resetMacros();
        }
        resetWorld();
    }

    public void startNewLevel(Skin skin) {

        currentHint = 0;
        ((BackgroundLayer)layerGroup.get("background")).changeForeground("screens/foreground.png");
        if (currentLevel.hasTutorial() && !currentLevel.equals("CREATE")) {
            ((HelpScreen) layerGroup.get("help screen")).setImages(currentLevel.getTutorialImages());
            layerGroup.setVisibility("help screen", true);
        }

        worldController.setupWorld(currentLevel);

        if (currentLevel.getType().equals("CREATE")) {
            worldController.addClickListenersToMap(creationMode, this);
        }

        worldController.initRender();

        if (currentLevel.allowMacro() && !currentLevel.getType().equals("CREATE")) {
            layerGroup.setVisibility("macro", true);
            layerGroup.setVisibility("inputs", false);
            layerGroup.setVisibility("create", false);
            layerGroup.setVisibility("tutorial", false);

            macroManager.resetMacros();
            macroManager.resetMacroInputs();

        } else if (currentLevel.getType().equals("CREATE")) {
            ((BackgroundLayer) layerGroup.get("background")).changeForegroundInFront("screens/test.png");

            layerGroup.setVisibility("macro", false);
            layerGroup.setVisibility("inputs", false);
            layerGroup.setVisibility("tutorial", false);
            layerGroup.setVisibility("create", true);
            ((CreateLayer) layerGroup.get("create")).reset();
            switch (Menu.createMode) {
                case WRITE: {
                    creationMode.createInputTiles("write");
                    creationMode.addGroup(new WriteModeLayer(skin, currentLevel));
                    break;
                }

                case READ: {
                    creationMode.createInputTiles("read");
                    creationMode.addGroup(new ReadModeLayer(skin, currentLevel));
                    break;
                }

                default: {
                    creationMode.createInputTiles("macro");
                    creationMode.addGroup(new Group());
                }
            }


        } else if (currentLevel.allowTutorial()) {
            ((BackgroundLayer) layerGroup.get("background")).changeForeground("screens/choices_foreground.png");

            layerGroup.setVisibility("create", false);
            layerGroup.setVisibility("macro", false);
            layerGroup.setVisibility("inputs", false);
            layerGroup.setVisibility("tutorial", true);

            choicesManager.setupChoices(currentLevel.getChoices());
            choicesManager.resetRules();
            choicesManager.setupRules(currentLevel.getNoRules(), skin, 1475, 1080 - 495);
            choicesManager.resetCheckboxes();
            choicesManager.setupCheckboxes(skin, currentLevel.getNoRules(), 1435, 1080 - 500);
            ((BackgroundLayer) layerGroup.get("background")).changeTutorialText(currentLevel.getTutText());

        } else {
            layerGroup.setVisibility("tutorial", false);
            layerGroup.setVisibility("create", false);
            layerGroup.setVisibility("macro", false);
            layerGroup.setVisibility("inputs", true);

            inputsManager.setupRules(currentLevel.getNoRules(), currentLevel.getRules(), false);
            inputsManager.setupInputs(currentLevel.getInputs(), 1415, 1080 - 165);

        }

        ((ControlsLayer)layerGroup.get("controls")).disableReset(currentLevel.allowRuleReset());
        ((ControlsLayer)layerGroup.get("controls")).disableHints(currentLevel.hasHints());
        ((ControlsLayer)layerGroup.get("controls")).disableHelp(!currentLevel.getType().equals("CREATE"));
        ((ControlsLayer)layerGroup.get("controls")).disableReset(!currentLevel.getType().equals("CREATE"));
        ((ControlsLayer)layerGroup.get("controls")).disableUploadButton(currentLevel.getType().equals("CREATE"));
        ((ControlsLayer)layerGroup.get("controls")).changeSubmitText(currentLevel.getType().equals("CREATE"));
        ((ControlsLayer)layerGroup.get("controls")).disableBackToCreate(isInPreview);

        ((BackgroundLayer)layerGroup.get("background")).changeText(currentLevel.getText());
        ((BackgroundLayer)layerGroup.get("background")).setMaxLights(worldController.getMaxObjects());

        if (currentLevel.hasTutorial()) {
            if(currentLevel.getType().equals("CREATE")) {
                creationMode.getLayer().group.addActorAfter(((CreateLayer)creationMode.getLayer()).getCreationGroup(),((HelpScreen)layerGroup.get("help screen")).group);
            } else {
                creationMode.getLayer().group.removeActor(((HelpScreen)layerGroup.get("help screen")).group);
            }
            ((HelpScreen)layerGroup.get("help screen")).setImages(currentLevel.getTutorialImages());
            layerGroup.setVisibility("help screen", true);
        }
    }

    public void render(float deltaTime) {
        ((ControlsLayer) layerGroup.get("controls")).disableSubmit(!inputsManager.checkRules());

        inputsManager.toggleLights();
        inputsManager.lightOffRules();
        inputsManager.lightOnRule(worldController.getCurrentRuleIndexes());

        macroManager.toggleLights();

        worldController.render(deltaTime);
        ((BackgroundLayer)layerGroup.get("background")).setNoLights(worldController.getMaxObjects() - worldController.getNoObjects());

        if (worldController.isLevelWon()) {
            currentLevel.save();
            ((WinningLayer)layerGroup.get("winning")).changeWinningButtons(isInPreview);
            layerGroup.setVisibility("winning", true);
        }

        if (worldController.isBobConfused()) {
            ((MessageLayer) layerGroup.get("message")).changeText("Uh oh, Bob does not know which rule to follow...");
            layerGroup.setVisibility("message", true);
        }

    }

    public void setLevel(Level level) {
        this.currentLevel = level;
    }

    public void loadNextLevel(Skin skin) {
        if (currentLevel.getNext() == null) { // Mode completed, back to menu
            hide();
        } else {
            currentLevel = currentLevel.getNext();
            startNewLevel(skin);
        }

        layerGroup.setVisibility("winning", false);
    }

    public void submit(Skin skin) {
        if (currentLevel.getType().equals("CREATE")) {
            currentLevel = creationMode.getCreatedLevel(worldController);
            isInPreview = true;
            startNewLevel(skin);
        } else {
            resetWorld();

            if (currentLevel.allowMacro()) {
                startLPSAnim(macroManager.getRulesString());
            } else if (currentLevel.allowTutorial()) {
                choicesManager.checkChoices(1365, 1080 - 495, currentLevel.getNoRules());
                if (choicesManager.areAllCheckedRulesCorrect() && choicesManager.areCorrectRulesComplete()) {
                    worldController.setLevelWon();
                } else {
                    layerGroup.setVisibility("tryAgain", true);
                    if (choicesManager.areAllCheckedRulesCorrect() && !choicesManager.areCorrectRulesComplete()) {
                        layerGroup.setVisibility("notComplete", true);
                    }
                }
            } else {
                if (inputsManager.mixedParadigmUsed()) {
                    ((HelpScreen) layerGroup.get("help screen")).setImage("screens/both_paradigm.png");
                    layerGroup.setVisibility("help screen", true);
                } else if (inputsManager.onlyConsequentUsed()) {
                    startMockAnim(inputsManager.getBlockStack());
                } else {
                    startLPSAnim(inputsManager.getRulesString());
                }
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
        isInPreview = false;
        layerGroup.hide();
    }

    public boolean isVisible() {
        return layerGroup.isVisible();
    }

    public boolean isLayerVisible(String layerName) {
        return layerGroup.get(layerName).isVisible();
    }

    public void displayHints() {
        if (currentLevel.hasHints()) {
            ((MessageLayer) layerGroup.get("message")).changeText(currentLevel.getHints()[currentHint]);
            layerGroup.setVisibility("message", true);
            currentHint = (currentHint + 1) % currentLevel.getHints().length;
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

    public void tryAgain(Skin skin) {
        choicesManager.resetCheckboxes();
    }

    public void changeCurrentLevelToCreate(){
        currentLevel.setType("CREATE");
    }

    public void goBackToCreate(Skin skin) {
        changeCurrentLevelToCreate();
        isInPreview = false;
        startNewLevel(skin);
        layerGroup.get("winning").setVisibility(false);
    }

    public void goBackToPlay() {
        layerGroup.get("winning").setVisibility(false);
    }

    public void setNameLevelOrLogIn() {
        if(gameStore.loggedIn) {
            layerGroup.get("setName").setVisibility(true);
        } else {
            layerGroup.get("logIn").setVisibility(true);
        }
    }

    public void uploadCurrentLevel(Skin skin, String name) {
        layerGroup.get("setName").setVisibility(false);
        System.out.print(creationMode.getXMLofLevel(worldController));
        gameStore.insertIntoDatabase(skin, creationMode.getXMLofLevel(worldController), name);
    }
}
