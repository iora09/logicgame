package com.bob.game.database;

import java.sql.*;

public class LocalDatabase implements Database {
    @Override
    public Connection connect(){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "system", "iora09");
            return connection;
        } catch (ClassNotFoundException|SQLException e) {
            throw new RuntimeException("Failed to connect to local database : ", e);
        }
    }

    @Override
    public ResultSet selectQuery(Connection connection, String sql){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL query in local db : ", e);
        }
    }

    @Override
    public int otherQuery(Connection connection, String sql){
        try {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL update/insert/delete in local db : ", e);
        }

    }
}
