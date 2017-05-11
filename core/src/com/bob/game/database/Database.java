package com.bob.game.database;

import java.sql.Connection;
import java.sql.ResultSet;

public interface Database {

    Connection connect();
    ResultSet selectQuery(Connection connection, String sql);
    int otherQuery(Connection connection, String sql);
}
