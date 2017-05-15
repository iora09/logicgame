package com.bob.game.database;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class LocalDatabase implements Database {
    @Override
    public Connection connect(DataSource ds){
        try {
            Connection connection = ds.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to local database : ", e);
        }
    }


    public static DataSource getDataSource(String dbType){
        BasicDataSource ds = new BasicDataSource();


        if("mysql".equals(dbType)){
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds.setUrl("jdbc:mysql://localhost:3306/db");
            ds.setUsername("root");
            ds.setPassword("iora09");
        }else if("oracle".equals(dbType)){
            ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            ds.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
            ds.setUsername("system");
            ds.setPassword("iora09");
        }else{
            return null;
        }

        return ds;
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
