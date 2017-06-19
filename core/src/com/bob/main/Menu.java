package com.bob.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bob.game.database.Database;
import com.bob.game.database.LocalDatabase;
import com.bob.game.levels.*;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.Connection;
import java.util.*;
import java.util.List;

public class Menu {

    private static final Group menuGroup = new Group();
    private static final Group modeGroup = new Group();
    private static final Group settingsGroup = new Group();
    private static final Group levelsGroup = new Group();
    private static Group gameStoreGroup = new GameStore();
    private static Group tutorialGroup = new Group();
    private static boolean isVisible = true;
    private static Level levelSelected;

    public Menu(Skin skin) {

        initMenu(skin);
        initLevels(skin);
        initMode(skin);
        initSettings(skin);
        initTutorials(skin);
        ((GameStore)gameStoreGroup).init(skin, false);
    }

    private void initTutorials(Skin skin) {
        Image tutBackground = new Image(TextureFactory.createTexture("screens/menu.png"));
        tutBackground.setBounds(0,0, 1920, 1080);
        tutorialGroup.addActor(tutBackground);

        addLevelButtons(tutorialGroup, skin, LevelFactory.TUTORIAL_CONTROLS, "tutorialProgress", "CONTROLS", 520);
        addLevelButtons(tutorialGroup, skin, LevelFactory.TUTORIAL_NOT, "tutorialProgress", "NEGATION", 390);
        addLevelButtons(tutorialGroup, skin, LevelFactory.TUTORIAL_AND, "tutorialProgress", "CONJUNCTION", 260);
        addBackButton(skin, tutorialGroup);

        tutorialGroup.setVisible(false);
    }

    private void initMenu(final Skin skin) {
        // Bkg
        Image menuBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        menuBkg.setBounds(0,0, 1920, 1080);
        menuGroup.addActor(menuBkg);

        // Menu button
        String[] menu = {"PLAY", "LEVELS", "SETTINGS", "QUIT"};

        Map<String, Button> buttons = addButtons(menuGroup, skin, menu);

        buttons.get("PLAY").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                modeGroup.setVisible(true);
            }
        });

        buttons.get("QUIT").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                Gdx.app.exit();
            }
        });

        buttons.get("LEVELS").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                levelsGroup.clear();
                initLevels(skin);
                levelsGroup.setVisible(true);
            }
        });

        buttons.get("SETTINGS").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                settingsGroup.setVisible(true);
            }
        });

    }

    private Map<String,Button> addButtons(Group group, Skin skin, String[] names) {
        Map<String, Button> buttons = new HashMap<>();
        int menuButtonY = 500;

        for (String buttonName : names) {

            TextButton button = new TextButton(buttonName, skin, "big_grey_button");
            button.setBounds(760, menuButtonY, 400, 100);

            buttons.put(buttonName, button);
            group.addActor(button);

            menuButtonY -= 120;
        }

        return buttons;
    }

    private void initMode(final Skin skin) {

        Image levelsBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        levelsBkg.setBounds(0,0, 1920, 1080);

        modeGroup.addActor(levelsBkg);

        String[] menu = {"WRITER", "READER", "MACRO", "TUTORIALS", "GAME STORE"};
        Map<String, Button> buttons = addButtons(modeGroup, skin, menu);

        buttons.get("WRITER").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("writeProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.WRITE.size() ? LevelFactory.WRITE.get(lvlIndex) : LevelFactory.WRITE.get(0));
            }
        });

        buttons.get("READER").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("readProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.READ.size() ? LevelFactory.READ.get(lvlIndex) : LevelFactory.READ.get(0));
            }
        });

        buttons.get("MACRO").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("macroProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.MACRO.size() ? LevelFactory.MACRO.get(lvlIndex) : LevelFactory.MACRO.get(0));
            }
        });

        buttons.get("TUTORIALS").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                /*
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("readProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.TUTORIAL.size() ? LevelFactory.TUTORIAL.get(lvlIndex) : LevelFactory.TUTORIAL.get(0));
                */
                tutorialGroup.clear();
                initTutorials(skin);
                tutorialGroup.setVisible(true);
            }
        });

        buttons.get("GAME STORE").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                ((GameStore)gameStoreGroup).initLevelsFromDb(skin);
                gameStoreGroup.setVisible(true);
            }
        });

        addBackButton(skin, modeGroup);

        modeGroup.setVisible(false);
    }

    private void initSettings(Skin skin) {

        // Bkg
        Image menuBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        menuBkg.setBounds(0,0, 1920, 1080);
        settingsGroup.addActor(menuBkg);

        TextButton button = new TextButton("RESET LEVELS", skin, "grey_button");
        button.setBounds(810, 430, 300, 100);

        button.addListener(
                new ClickListener() {
                   public void clicked(InputEvent ie, float x, float y) {
                       Preferences prefs = Gdx.app.getPreferences("Progress");
                       prefs.putInteger("writeProgress", -1);
                       prefs.putInteger("readProgress", -1);
                       prefs.putInteger("macroProgress", -1);
                       prefs.putInteger("tutorialProgress", -1);
                   }
               }
        );
        settingsGroup.addActor(button);

        addBackButton(skin, settingsGroup);

        settingsGroup.setVisible(false);
    }

    public static void launchLevel(Level level) {
        levelSelected = level;
        hide();
    }

    private void initLevels(Skin skin) {
        // Levels Menu
        Image levelsBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        levelsBkg.setBounds(0,0, 1920, 1080);
        levelsGroup.addActor(levelsBkg);

        addLevelButtons(levelsGroup, skin, LevelFactory.WRITE, "writeProgress", "Write", 520);
        addLevelButtons(levelsGroup, skin, LevelFactory.READ, "readProgress", "Read", 390);
        addLevelButtons(levelsGroup, skin, LevelFactory.MACRO, "macroProgress", "Macro", 260);

        addBackButton(skin, levelsGroup);

        levelsGroup.setVisible(false);
    }

    private void addLevelButtons(Group group, Skin skin, final List<Level> levels, String prefString, String title, int startY) {
        int noLevels = levels.size();
        int levelsButtonX = 660;
        int levelsButtonY = startY;

        Texture lockTexture;

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("impact");
        Label titleLabel = new Label(title, titleStyle);
        titleLabel.setAlignment(Align.right);
        titleLabel.setBounds(0, startY, 640, 100);

        group.addActor(titleLabel);

        Preferences prefs = Gdx.app.getPreferences("Progress");
        int unlocked = prefs.getInteger(prefString, -1);


        for (int i = 0; i < noLevels; i++) {
            final int j = i;
            TextButton button = new TextButton(levels.get(i).getLevelName(), skin, "grey_square_button");
            button.setBounds(levelsButtonX, levelsButtonY, 100, 100);

            button.addListener(new ClickListener() {
                public void clicked(InputEvent ie, float x, float y) {
                    launchLevel(levels.get(j));
                }
            });

            group.addActor(button);

            // Disable if not unlocked
            /*if (i > unlocked + 1 && Config.levelsAreLocked) {
                button.setDisabled(true);
                Image lock = new Image(lockTexture);
                lock.setBounds(levelsButtonX - 14, levelsButtonY - 14, 128, 128);
                group.addActor(lock);
            }*/


            levelsButtonX += 125;

            /*if (levelsButtonX >= 1360) {
                levelsButtonY -= 125;
                levelsButtonX = 560;
            }       */
        }

    }

    public static void addBackButton(Skin skin, final Group group) {
        TextButton backButton = new TextButton("BACK", skin, "grey_button");
        backButton.setBounds(10, 15, 200, 60);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                group.setVisible(false);
            }
        });

        group.addActor(backButton);
    }

    public void show() {
        menuGroup.setVisible(true);
        isVisible = true;
    }

    public static void hide() {
        menuGroup.setVisible(false);
        modeGroup.setVisible(false);
        levelsGroup.setVisible(false);
        tutorialGroup.setVisible(false);
        settingsGroup.setVisible(false);
        gameStoreGroup.setVisible(false);
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Level getLevelSelected() {
        return levelSelected;
    }

    public void setStage(Stage stage) {
        stage.addActor(menuGroup);
        stage.addActor(levelsGroup);
        stage.addActor(modeGroup);
        stage.addActor(tutorialGroup);
        stage.addActor(settingsGroup);
        stage.addActor(gameStoreGroup);
    }
}
