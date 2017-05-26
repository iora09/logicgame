package com.bob.main;

import com.bob.game.database.Database;
import com.bob.game.database.LocalDatabase;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;

public class User {
    private String username;
    private List<Level> games = new ArrayList<>();

    public User(String username) {
        this.username = username;
        populateGames();
    }

    public String getUsername() {
        return username;
    }

    public void populateGames() {
        Database db = new LocalDatabase();
        Connection connection = db.connect(((LocalDatabase) db).getDataSource("mysql"));
        ResultSet rs = db.selectQuery(connection, "SELECT * FROM games JOIN users ON games.game_user = users.username " +
                "WHERE users.username='" + username + "'");
        try {
            while(rs.next()) {
                String gameName = rs.getString("game_name");
                String gameXML = rs.getString("game_xml");
                games.add(LevelFactory.createLevel(gameXML, gameName));
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't retrieve user's games due to SQL error: ", e);
        }
    }
}
