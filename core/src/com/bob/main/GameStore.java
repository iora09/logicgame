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
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.EmailValidator;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;


public class GameStore extends Group {

    public static boolean loggedIn = false;
    private static User user;
    private final Group gamesGroup = new Group();
    private final Group logInGroup = new Group();
    private final Group registerGroup = new Group();
    private final Group gameOptionsGroup = new Group();
    private Label errorLabel;
    private Database db;

    public GameStore(Database db) {
        this.db = db;
    }

    public void init(Skin skin, boolean visibility) {
        this.clear();
        Image background = new Image(TextureFactory.createTexture("screens/games_store.png"));
        background.setBounds(0,0, 1920, 1080);
        this.addActor(background);
        Menu.addBackButton(skin, this);
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
        initLevelsFromDb(skin);
        this.addActor(gamesGroup);
        gamesGroup.setVisible(true);
        this.addActor(gameOptionsGroup);
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
                if (gameOptionsGroup.hit(x, y, true) == null) {
                    gameOptionsGroup.setVisible(false);
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
                if (loggedIn) {
                    FileChooser fileChooser = new FileChooser("Choose game to import", skin) {
                        @Override
                        protected void result(Object object) {
                            if (object.equals("OK")) {
                                FileHandle fileHandle = getFile();
                                if (fileHandle != null && fileHandle.exists() && !fileHandle.isDirectory()
                                        && fileHandle.extension().equals("xml")) {
                                    insertIntoDatabase(skin, fileHandle);
                                } else if (object.equals("Cancel")) {
                                    this.setVisible(false);
                                }
                            }
                        }
                    };
                    fileChooser.setDirectory(new FileHandle(Gdx.files.getExternalStoragePath()));
                    fileChooser.setBounds(1400, 100, 400, 400);
                    addFileChooser(fileChooser);
                } else {
                    logInGroup.setVisible(true);
                }
            }
        });
        this.addActor(importButton);
    }

    private void addFileChooser(FileChooser fileChooser) {
        this.addActor(fileChooser);
    }

    public void initLevelsFromDb(Skin skin) {
        gamesGroup.clearChildren();
        Label allGamesLabel = new Label("All Games:", skin, "label_style");
        allGamesLabel.setBounds(50,870,100, 50);
        gamesGroup.addActor(allGamesLabel);
        List<Level> allGames = getAllGames();
        Table allGamesTable = new Table(skin);
        for(Level game : allGames) {
            addGame(allGamesTable, game, game.getOwner(), skin);
            allGamesTable.row();
        }
        ScrollPane allGamesPane = new ScrollPane(null, skin, "scroll");
        allGamesPane.setBounds(10, 80, 600, 750);
        allGamesPane.setScrollingDisabled(true,false);
        allGamesPane.setWidget(allGamesTable);
        gamesGroup.addActor(allGamesPane);

        if (loggedIn) {
            Label usersGamesLabel = new Label("Your games:", skin, "label_style");
            usersGamesLabel.setBounds(1000,870,100, 50);
            gamesGroup.addActor(usersGamesLabel);
            List<Level> usersGames = user.getGames();
            if (usersGames.isEmpty()) {
                Label noGames = new Label("You have no games!", skin, "info_small_label");
                noGames.setBounds(1050, 600, 150, 50);
                gamesGroup.addActor(noGames);
            } else {
                Table usersGamesTable = new Table(skin);
                for (Level game : usersGames) {
                    addGame(usersGamesTable, game, user.getUsername(), skin);
                    usersGamesTable.row();
                }
                ScrollPane usersGamesPane = new ScrollPane(null, skin, "scroll");
                usersGamesPane.setBounds(1000, 80, 600, 750);
                usersGamesPane.setScrollingDisabled(true, false);
                usersGamesPane.setWidget(usersGamesTable);
                gamesGroup.addActor(usersGamesPane);
            }
        }
    }

    private void addGame(Table table, final Level game, final String owner, final Skin skin) {
        Image gameImage = new Image(TextureFactory.createTexture("thumbs/bob.png"));
        gameImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent ie, float x, float y) {
                if (user != null && (user.getUsername().equals(owner) || user.getUsername().equals("admin"))) {
                    createAndShowGameOptions(game, skin);
                } else {
                    Menu.launchLevel(game);
                }
            }
        });
        String date = game.getDate()==null ? "NaN" : game.getDate().toString();
        Label infoLabel = new Label("Name : " + game.getLevelName() + "\n" + "Type : " + game.getType() + "\n"
                + "Created by : " + owner + "\n" + "Date : " + date, skin, "info_small_label");
        table.add(gameImage).width(200).height(150).expandX().padBottom(40);
        table.add(infoLabel).width(350).height(170).padBottom(40);
    }

    private void createAndShowGameOptions(final Level game, final Skin skin) {
        gameOptionsGroup.clearChildren();
        Image gameOptionsPanel = new Image(TextureFactory.createTexture("screens/modal.png"));
        gameOptionsPanel.setBounds(640, 400, 600, 380);
        gameOptionsGroup.addActor(gameOptionsPanel);

        TextButton playButton = new TextButton("PLAY", skin, "big_grey_button");
        TextButton editButton = new TextButton("EDIT", skin, "big_grey_button");
        TextButton deleteButton = new TextButton("DELETE", skin, "big_grey_button");

        playButton.setBounds(800, 640, 250, 100);
        editButton.setBounds(800, 530, 250, 100);
        deleteButton.setBounds(800, 420, 250, 100);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameOptionsGroup.setVisible(false);
                Menu.launchLevel(game);
            }
        });

        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameOptionsGroup.setVisible(false);
                deleteFromDatabase(game, skin);
            }
        });

        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameOptionsGroup.setVisible(false);
                Menu.Create createMode;
                switch (game.getType()) {
                    case "WRITE" : {
                        createMode = Menu.Create.WRITE;
                        break;
                    }
                    case "READ" : {
                        createMode = Menu.Create.READ;
                        break;
                    }
                    case "MACRO" : {
                        createMode = Menu.Create.MACRO;
                        break;
                    }
                    default:
                        createMode = Menu.Create.NOTHING;
                }
                game.resetTutorials();
                Menu.createMode = createMode;
                game.setType("CREATE");
                Menu.launchLevel(game);
            }
        });

        gameOptionsGroup.addActor(playButton);
        gameOptionsGroup.addActor(editButton);
        gameOptionsGroup.addActor(deleteButton);
        gameOptionsGroup.setVisible(true);
    }

    private void deleteFromDatabase(Level game, Skin skin) {
        Connection connection = db.connect(db.getDataSource("mysql"));
        try {
            db.otherQuery(connection, "DELETE FROM games WHERE game_name='" + game.getLevelName() + "'");
            user.populateGames(db);
            initLevelsFromDb(skin);
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot delete game due to SQL error :" + e);
        }
    }

    public void insertIntoDatabase(Skin skin, String xmlString, String xmlName) {
        Connection connection = db.connect(db.getDataSource("mysql"));
        xmlString = xmlString.replace("'", "''");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            db.otherQuery(connection, "INSERT INTO games \n VALUES ("
                    + "'"
                    + xmlName
                    + "'"
                    + ","
                    + "'"
                    + xmlString
                    + "'"
                    + ","
                    + "'"
                    + user.getUsername()
                    + "'"
                    + ","
                    + "'"
                    + dateFormat.format(new Date())
                    + "'"
                    + ") ON DUPLICATE KEY UPDATE game_xml = '" + xmlString + "', game_user = '" + user.getUsername()
                    + "', game_date = '" + dateFormat.format(new Date()) + "'");

            connection.close();
        } catch(SQLException e) {
            throw new RuntimeException("Can't close connection: " + e);
        }
        user.populateGames(db);
        initLevelsFromDb(skin);
    }
    private void insertIntoDatabase(Skin skin, FileHandle fileHandle) {
        insertIntoDatabase(skin, fileHandle.readString(), fileHandle.nameWithoutExtension());
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
                tryLoggingIn(logInGroup, usernameField.getText(), passwordField.getText(), skin, true);
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

    public void tryLoggingIn(Group group, String username, String password, Skin skin, boolean visibility) {
        String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
        Connection connection = db.connect(db.getDataSource("mysql"));
        ResultSet rs = db.selectQuery(connection, "SELECT * FROM users "
                + "WHERE users.username='" + username + "'"
                + " AND "
                + "users.password='" + hashedPassword + "'");
        try {
            if(rs.next()) {
                loggedIn = true;
                user = new User(username, db);
                removeError(group);
                group.setVisible(false);
                init(skin, visibility);
            } else {
                addError("Username or password not recognised!", group, skin);
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
            removeError(registerGroup);
            addError("All fields must be completed!", registerGroup, skin);
        } else if (EmailValidator.getInstance().isValid(email)){
            String hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            Connection connection = db.connect(db.getDataSource("mysql"));
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

    public List<Level> getAllGames() {
        Connection connection = db.connect(db.getDataSource("mysql"));
        ResultSet rs = db.selectQuery(connection, "SELECT * FROM games");
        List<Level> allGames = new ArrayList<>();
        try {
            while (rs.next()) {
                String gameName = rs.getString("game_name");
                String gameXML = rs.getString("game_xml");
                String gameOwner = rs.getString("game_user");
                java.sql.Date gameDate = rs.getDate("game_date");
                Level newLevel = LevelFactory.createLevel(gameXML, gameName);
                newLevel.setOwner(gameOwner);
                newLevel.setDate(gameDate);
                allGames.add(newLevel);
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch the games due to an sql error: " + e);
        }
        return allGames;
    }

    public Group getLogInGroup() {
        return logInGroup;
    }
}
