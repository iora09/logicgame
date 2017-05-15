package com.bob.game.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

public interface Database {

    Connection connect(DataSource ds);
    ResultSet selectQuery(Connection connection, String sql);
    int otherQuery(Connection connection, String sql);
}
