package com.bob.game.database;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OnlineDatabase implements Database{
    @Override
    public Connection connect(DataSource ds){
        try {
            Connection connection = ds.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to online database : ", e);
        }
    }


    public DataSource getDataSource(String dbType){
        BasicDataSource ds = new BasicDataSource();
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("db.prop"));
        } catch (IOException e) {
            throw new RuntimeException("The database properties file(db.prop) is not found or could not be opened: " + e);
        }

        if("mysql".equals(dbType)){
            ds.setDriverClassName(prop.getProperty("ONLINE_MYSQL_DB_DRIVER_CLASS"));
            ds.setUrl(prop.getProperty("ONLINE_MYSQL_DB_URL"));
            ds.setUsername(prop.getProperty("ONLINE_MYSQL_DB_USER"));
            ds.setPassword(prop.getProperty("ONLINE_MYSQL_DB_PASSWORD"));
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
            throw new RuntimeException("Failed to execute SQL query in online db : ", e);
        }
    }

    @Override
    public int otherQuery(Connection connection, String sql) throws SQLException{
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }
}
