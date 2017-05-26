package com.bob.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.FileChooser;
import com.bob.game.database.Database;
import com.bob.game.database.LocalDatabase;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.EmailValidator;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;


public class GameStore extends Group {

    private static boolean loggedIn = false;
    private static User user;
    private final Group gamesGroup = new Group();
    private final Group logInGroup = new Group();
    private final Group registerGroup = new Group();
    private Label errorLabel;

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
        initRegisterGroup(skin);
        this.addActor(registerGroup);
        gamesGroup.setVisible(true);
        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (logInGroup.hit(x, y, true) == null) {
                    removeError(logInGroup);
                    logInGroup.setVisible(false);
                }
                if (registerGroup.hit(x, y, true) == null) {
                    removeError(registerGroup);
                    registerGroup.setVisible(false);
                }
                return false;
            }
        });
        this.setVisible(visibility);
    }

    private void addLogOutButton(final Skin skin) {
        Label userLabel = new Label("Welcome, " + user.getUsername(), skin, "label_style");
        userLabel.setBounds(1500, 1000, 200, 60);
        TextButton logOutButton = new TextButton("LOG OUT", skin, "grey_button");
        logOutButton.setBounds(1600, 930, 200, 60);
        logOutButton.addListener(new ClickListener() {
            @Override
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
            @Override
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
        ScrollPane allGamesPane = new ScrollPane(null, skin, "scroll");
        allGamesPane.setBounds(10, 80, 800, 800);
        allGamesPane.setScrollingDisabled(true,false);
        Group allGames = new Group();
        allGames.setBounds(10,80,800, 1500);
        //List<Level> allGames = getAllGames();
        int x = 50;
        int y = 1300;
        int moveY = 200;
        try {
            while (rs.next()) {
                String gameName = rs.getString("game_name");
                String gameXML = rs.getString("game_xml");
                String gameOwner = rs.getString("game_user");
                addGame(allGames, gameName, gameXML, gameOwner, skin, x, y);
                y = y - moveY;
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch the games due to an sql error: " + e);
        }
        allGamesPane.setWidget(allGames);
        gamesGroup.addActor(allGamesPane);

        if (loggedIn) {

        }
    }

    private void addGame(Group group, String gameName, String gameXML, String gameOwner, Skin skin, int x, int y) {
        //Image gameImage = new Image();
        TextButton gameImage = new TextButton("PLAY", skin, "big_grey_button");
        gameImage.setBounds(x, y, 200, 150);
        final Level level = LevelFactory.createLevel(gameXML, gameName);
        gameImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ie, float x, float y) {
                Menu.launchLevel(level);
            }
        });
        Label infoLabel = new Label("Name : " +gameName + "\n"
                + "Type : " + level.getType() + "\n"
                + "Created by : " + gameOwner, skin, "info_label");
        infoLabel.setBounds(x + 210, y, 200, 150);
        group.addActor(gameImage);
        group.addActor(infoLabel);
    }

    private void insertIntoDatabase(FileHandle fileHandle) {
        Database db = new LocalDatabase();
        Connection connection = db.connect(((LocalDatabase)db).getDataSource("mysql"));
        String xmlString = fileHandle.readString();
        xmlString = xmlString.replace("'", "''");
        try {
            db.otherQuery(connection, "INSERT INTO games \n VALUES ("
                    + "'"
                    + fileHandle.nameWithoutExtension()
                    + "'"
                    + ","
                    + "'"
                    + xmlString
                    + "'"
                    + ","
                    + "'READ') ON DUPLICATE KEY UPDATE game_xml = '" + xmlString + "', game_user = 'READ'");

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

        final Label registerLabel = new Label("Not registered? Sign Up!", skin, "light_grey_label");
        registerLabel.setBounds(840, 410, 300, 20);
        registerLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logInGroup.setVisible(false);
                registerGroup.setVisible(true);
            }
        });

        registerLabel.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                registerLabel.setStyle(skin.get("over_label", Label.LabelStyle.class));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                registerLabel.setStyle(skin.get("light_grey_label", Label.LabelStyle.class));
            }
        });

        TextButton logInButton = new TextButton("Log in", skin, "grey_button");
        logInButton.setBounds(840, 450, 200, 60);
        logInButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ie, float x, float y) {
                tryLoggingIn(usernameField.getText(), passwordField.getText(), skin);
                registerGroup.setVisible(false);
            }
        });

        logInGroup.addActor(usernameLabel);
        logInGroup.addActor(passwordLabel);
        logInGroup.addActor(usernameField);
        logInGroup.addActor(passwordField);
        logInGroup.addActor(logInButton);
        logInGroup.addActor(registerLabel);
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
                removeError(logInGroup);
                logInGroup.setVisible(false);
                init(skin, true);
            } else {
                addError("Username or password not recognised!", logInGroup, skin);
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Can't log in due to a SQL error: ", e);
        }
    }

    private void addError(String error, Group group, Skin skin) {
        errorLabel = new Label(error, skin, "red_label");
        errorLabel.setBounds(700, 730, 500, 30);
        group.addActor(errorLabel);
    }

    private void removeError(Group group) {
        group.removeActor(errorLabel);
    }

    private void initRegisterGroup(final Skin skin) {
        Image registerPanel = new Image(TextureFactory.createTexture("screens/modal.png"));
        registerPanel.setBounds(640, 300, 600, 500);
        registerGroup.addActor(registerPanel);

        Label usernameLabel = new Label("Username", skin, "label_style");
        Label passwordLabel = new Label("Password", skin.get("label_style", Label.LabelStyle.class));
        Label emailLabel = new Label("E-mail", skin, "label_style");

        usernameLabel.setBounds(660, 650, 200, 50);
        passwordLabel.setBounds(660, 550, 200, 50);
        emailLabel.setBounds(660, 450, 200, 50);

        final TextField usernameField = new TextField("", skin);
        final TextField passwordField = new TextField("", skin);
        final TextField emailField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        usernameField.setBounds(900, 650, 300, 50);
        passwordField.setBounds(900, 550, 300, 50);
        emailField.setBounds(900,450,300, 50);

        TextButton registerButton = new TextButton("Register", skin, "grey_button");
        registerButton.setBounds(860, 310, 200, 60);
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tryRegistering(usernameField.getText(), passwordField.getText(), emailField.getText(), skin);
            }
        });

        registerGroup.addActor(usernameLabel);
        registerGroup.addActor(passwordLabel);
        registerGroup.addActor(emailLabel);
        registerGroup.addActor(usernameField);
        registerGroup.addActor(passwordField);
        registerGroup.addActor(emailField);
        registerGroup.addActor(registerButton);
        registerGroup.setVisible(false);
    }

    private void tryRegistering(String username, String password, String email, Skin skin) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            addError("All fields must be completed!", registerGroup, skin);
        } else if (EmailValidator.getInstance().isValid(email)){
            String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            Database db = new LocalDatabase();
            Connection connection = db.connect(((LocalDatabase) db).getDataSource("mysql"));
            try {
                db.otherQuery(connection, "INSERT INTO users \n VALUES( "
                        + "'"
                        + username
                        + "'"
                        + ","
                        + "'"
                        + hashedPassword
                        + "'"
                        + ","
                        + "'"
                        + email
                        + "')");
                removeError(registerGroup);
                registerGroup.setVisible(false);
                connection.close();
            } catch (SQLException e) {
                if (e instanceof SQLIntegrityConstraintViolationException) {
                    if (e.getMessage().contains("PRIMARY")) {
                        removeError(registerGroup);
                        addError("Username already in use, please choose a new one!", registerGroup, skin);
                    } else {
                        removeError(registerGroup);
                        addError("Email address already in use!", registerGroup, skin);
                    }
                }
            }
        } else {
            removeError(registerGroup);
            addError("Please provide a valid email address!", registerGroup, skin);
        }
    }

}
