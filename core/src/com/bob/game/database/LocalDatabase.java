package com.bob.game.database;

import com.badlogic.gdx.Gdx;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.Properties;

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


    public DataSource getDataSource(String dbType){
        BasicDataSource ds = new BasicDataSource();
        Properties prop = new Properties();
        try {
            prop.load(Gdx.files.internal("db/db.prop").read());
        } catch (IOException e) {
            throw new RuntimeException("The database properties file(db.prop) is not found or could not be opened: " + e);
        }

        if("mysql".equals(dbType)){
            ds.setDriverClassName(prop.getProperty("MYSQL_DB_DRIVER_CLASS"));
            ds.setUrl(prop.getProperty("MYSQL_DB_URL"));
            ds.setUsername(prop.getProperty("MYSQL_DB_USER"));
            ds.setPassword(prop.getProperty("MYSQL_DB_PASSWORD"));
        }else if("oracle".equals(dbType)){
            ds.setDriverClassName(prop.getProperty("ORACLE_DB_DRIVER_CLASS"));
            ds.setUrl(prop.getProperty("ORACLE_DB_URL"));
            ds.setUsername(prop.getProperty("ORACLE_DB_USER"));
            ds.setPassword(prop.getProperty("ORACLE_DB_PASSWORD"));
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
    public int otherQuery(Connection connection, String sql) throws SQLException{
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }
}
