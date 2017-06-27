package com.bob.game.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {

    Connection connect(DataSource ds);
    ResultSet selectQuery(Connection connection, String sql);
    DataSource getDataSource(String dbType);
    int otherQuery(Connection connection, String sql) throws SQLException;
}
