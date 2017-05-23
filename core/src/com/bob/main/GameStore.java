package com.bob.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;
import com.bob.game.FileChooser;
import com.bob.game.database.Database;
import com.bob.game.database.LocalDatabase;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;



public class GameStore extends Group {

    private static boolean loggedIn = false;
    private static User user;
    private final Group gamesGroup = new Group();
    private final Group logInGroup = new Group();

    public GameStore() {
    }

    public void init(Skin skin, boolean visibility) {
        Image background = new Image(TextureFactory.createTexture("screens/games_store.png"));
        background.setBounds(0,0, 1920, 1080);
        this.addActor(background);
        Menu.addBackButton(skin, this);
        initLevelsFromDb(skin);
        this.addActor(gamesGroup);
        addImportButton(skin);
        if (loggedIn) {
            addLogOutButton(skin);
        } else {
            initLogInGroup(skin);
            addLogInButton(skin);
        }
        this.addActor(logInGroup);
        gamesGroup.setVisible(true);
        this.setVisible(visibility);
    }
    private void addLogOutButton(final Skin skin) {
        Label userLabel = new Label("Welcome, " + user.getUsername(), skin, "label_style");
        userLabel.setBounds(1500, 1000, 200, 60);
        TextButton logOutButton = new TextButton("LOG OUT", skin, "grey_button");
        logOutButton.setBounds(1600, 930, 200, 60);
        logOutButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                loggedIn = false;
                user = null;
                init(skin, true);
            }
        });
        this.addActor(userLabel);
        this.addActor(logOutButton);
    }

    private void addLogInButton(Skin skin) {
        TextButton logInButton = new TextButton("LOG IN", skin, "grey_button");
        logInButton.setBounds(1600, 1000, 200, 60);
        logInButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                logInGroup.setVisible(true);
            }
        });
        this.addActor(logInButton);
    }

    private void addImportButton(final Skin skin) {
        final TextButton importButton = new TextButton("IMPORT", skin, "grey_button");
        importButton.setBounds(1600, 15, 200, 60);
        importButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                FileChooser fileChooser = new FileChooser("Choose game to import", skin) {
                    @Override
                    protected void result(Object object) {
                        if (object.equals("OK")) {
                            FileHandle fileHandle = getFile();
                            if (fileHandle != null && fileHandle.exists() && !fileHandle.isDirectory()
                                    && fileHandle.extension().equals("xml")) {
                                insertIntoDatabase(fileHandle);
                                initLevelsFromDb(skin);
                            } else if (object.equals("Cancel")) {
                                this.setVisible(false);
                            }
                        }
                    }
                };
                fileChooser.setDirectory(new FileHandle(Gdx.files.getExternalStoragePath()));
                fileChooser.setBounds(1400, 100, 400, 400);
                addFileChooser(fileChooser);
            }
        });
        this.addActor(importButton);
    }

    private void addFileChooser(FileChooser fileChooser) {
        this.addActor(fileChooser);
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

    private void initLogInGroup(final Skin skin) {
        Image logInPanel = new Image(TextureFactory.createTexture("screens/modal.png"));
        logInPanel.setBounds(640, 400, 600, 380);
        logInGroup.addActor(logInPanel);

        Label usernameLabel = new Label("Username", skin, "label_style");
        Label passwordLabel = new Label("Password", skin.get("label_style", Label.LabelStyle.class));

        usernameLabel.setBounds(660, 650, 200, 50);
        passwordLabel.setBounds(660, 550, 200, 50);

        final TextField usernameField = new TextField("", skin);
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        usernameField.setBounds(900, 650, 300, 50);
        passwordField.setBounds(900, 550, 300, 50);

        TextButton logInButton = new TextButton("Log in", skin, "grey_button");
        logInButton.setBounds(870, 410, 200, 60);
        logInButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                tryLoggingIn(usernameField.getText(), passwordField.getText(), skin);
                logInGroup.setVisible(false);
            }
        });

        logInGroup.addActor(usernameLabel);
        logInGroup.addActor(passwordLabel);
        logInGroup.addActor(usernameField);
        logInGroup.addActor(passwordField);
        logInGroup.addActor(logInButton);
        logInGroup.setVisible(false);
    }

    private void tryLoggingIn(String username, String password, Skin skin) {
        String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        Database db = new LocalDatabase();
        Connection connection = db.connect(((LocalDatabase)db).getDataSource("mysql"));
        ResultSet rs = db.selectQuery(connection, "SELECT * FROM users "
                + "WHERE users.username='" + username + "'"
                + " AND "
                + "users.password='" + hashedPassword + "'");
        try {
            if(rs.next()) {
                loggedIn = true;
                user = new User(username);
                init(skin, true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't log in due to a SQL error: ", e);
        }
    }
}
