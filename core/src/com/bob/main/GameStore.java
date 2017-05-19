package com.bob.main;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;
import com.bob.game.database.Database;
import com.bob.game.database.LocalDatabase;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameStore extends Group {

    private final Group importGroup = new Group();
    private final Group gamesGroup = new Group();

    public GameStore(Skin skin) {
        Image background = new Image(TextureFactory.createTexture("screens/games_store.png"));
        background.setBounds(0,0, 1920, 1080);
        this.addActor(background);
        Menu.addBackButton(skin, this);
        addImportButton(skin);
        initImportButton(skin);
        this.addActor(importGroup);
        initLevelsFromDb(skin);
        this.addActor(gamesGroup);
        gamesGroup.setVisible(true);
    }

    private void addImportButton(Skin skin) {
        final TextButton importButton = new TextButton("IMPORT", skin, "grey_button");
        importButton.setBounds(1600, 15, 200, 60);
        importButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                importGroup.setVisible(true);
            }
        });
        this.addActor(importButton);
    }

    public void initLevelsFromDb(Skin skin) {
        Database db = new LocalDatabase();
        Connection connection = db.connect(((LocalDatabase)db).getDataSource("mysql"));
        ResultSet rs = db.selectQuery(connection, "SELECT * FROM games");
        gamesGroup.clearChildren();
        int x = 50;
        int y = 700;
        int moveY = 130;
        try {
            while (rs.next()) {
                String gameName = rs.getString("game_name");
                String gameXML = rs.getString("game_xml");
                String gameType = rs.getString("game_type");
                addGame(gameName, gameXML, skin, x, y);
                y = y - moveY;
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch the games due to an sql error: " + e);
        }
    }

    private void addGame(String gameName, String gameXML, Skin skin, int x, int y) {
        //Image gameImage = new Image();
        TextButton gameImage = new TextButton("PLAY", skin, "big_grey_button");
        gameImage.setBounds(x, y, 200, 100);
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element root = xmlReader.parse(gameXML);
        String gameType = root.getAttribute("type");
        final Level level = LevelFactory.createLevel(root.getAttribute("type"), root, gameName);
        gameImage.addListener(new ClickListener() {
          public void clicked(InputEvent ie, float x, float y) {
              Menu.launchLevel(level);
          }
        });
        Label.LabelStyle infoStyle = new Label.LabelStyle();
        infoStyle.font = skin.getFont("white");
        Label infoLabel = new Label("Name : " +gameName + "\n" + "Type : " + gameType, infoStyle);
        infoLabel.setBounds(x + 210, y, 200, 100);
        gamesGroup.addActor(gameImage);
        gamesGroup.addActor(infoLabel);
    }


    private void initImportButton(final Skin skin) {
            Image background = new Image(TextureFactory.createTexture("screens/games_store.png"));
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
                    FileHandle fileHandle = new FileHandle(importTextField.getText());
                    if (fileHandle.exists() && !fileHandle.isDirectory() && fileHandle.extension().equals("xml")) {
                        insertIntoDatabase(fileHandle);
                        initLevelsFromDb(skin);
                        importGroup.setVisible(false);
                    }
                }
            });
            importGroup.addActor(submitButton);
            Menu.addBackButton(skin, importGroup);
            importGroup.setVisible(false);
    }

    private void insertIntoDatabase(FileHandle fileHandle) {
        Database db = new LocalDatabase();
        Connection connection = db.connect(((LocalDatabase)db).getDataSource("mysql"));
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
