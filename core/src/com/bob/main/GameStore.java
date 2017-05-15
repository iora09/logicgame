package com.bob.main;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.database.Database;
import com.bob.game.database.LocalDatabase;

import java.sql.Connection;
import java.sql.SQLException;

public class GameStore extends Group {

    private final Group importGroup = new Group();

    public GameStore(Skin skin) {
        Image background = new Image(TextureFactory.createTexture("screens/menu.png"));
        background.setBounds(0,0, 1920, 1080);
        Label label = new Label("GAME STORE", skin);
        label.setBounds(800,1080, 200, 70 );
        this.addActor(background);
        this.addActor(label);
        initLevelsFromDb(skin);
        initImportButton(skin);

        TextButton importButton = new TextButton(" + IMPORT", skin, "big_grey_button");
        importButton.setBounds(800, 980, 200, 70);
        this.addActor(importButton);
        importButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                importGroup.setVisible(true);
            }
        });

    }

    private void initLevelsFromDb(Skin skin) {
    }

    private void initImportButton(final Skin skin) {
            Image background = new Image(TextureFactory.createTexture("screens/menu.png"));
            background.setBounds(0,0, 1920, 1080);
            importGroup.addActor(background);
            final TextField importTextField = new TextField("Import Path", skin);
            importTextField.setBounds(760, 430, 400, 50);
            importGroup.addActor(importTextField);
            TextButton submitButton = new TextButton("Submit", skin);
            submitButton.setBounds(1160, 430, 100,50 );
            submitButton.addListener(new ClickListener() {
                public void clicked(InputEvent ie, float x, float y) {
                    //LevelFactory.loadExternaLevel(importTextField.getText());
                    insertIntoDatabase(importTextField.getText());
                    initLevelsFromDb(skin);
                    importGroup.setVisible(false);
                }
            });
            importGroup.addActor(submitButton);
            Menu.addBackButton(skin, importGroup);
            importGroup.setVisible(false);
    }

    private void insertIntoDatabase(String path) {
        Database db = new LocalDatabase();
        Connection connection = db.connect(((LocalDatabase)db).getDataSource("mysql"));
        FileHandle fileHandle = new FileHandle(path);
        String xmlString = fileHandle.readString();
        xmlString = xmlString.replace("'", "''");
        db.otherQuery(connection, "INSERT INTO games \n VALUES ("
                + "'"
                + fileHandle.nameWithoutExtension()
                + "'"
                + ","
                + "'"
                + xmlString
                + "'"
                + ","
                + "'READ') ON DUPLICATE KEY UPDATE game_xml = '" + xmlString + "', game_type = 'READ'" );
        try {
            connection.close();
        } catch(SQLException e) { 
            throw new RuntimeException("Can't close connection: " + e); 
        }
    }
}
