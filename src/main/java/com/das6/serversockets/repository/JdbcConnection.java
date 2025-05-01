package com.das6.serversockets.repository;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnection {
    private static final JdbcConnection INSTANCE = new JdbcConnection();
    private Properties properties;
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String dbDriver;

    private void loadProperties() {
        properties = new Properties();
        try {
            properties.load(Repository.class.getClassLoader().getResourceAsStream("config.properties"));
            dbHost = properties.getProperty("DB_HOSTNAME");
            dbPort = properties.getProperty("DB_COMMUNICATION_PORT");
            dbName = properties.getProperty("DB_NAME");
            dbUser = properties.getProperty("DB_USERNAME");
            dbPassword = properties.getProperty("DB_PASSWORD");
            dbDriver = properties.getProperty("DB_DRIVER");
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private JdbcConnection() {
        loadProperties();
    }

    public static JdbcConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        if(dbDriver == null) {
            throw new IllegalArgumentException("There are missing properties in the config file.");
        }
        return establishConnection();
    }

    private Connection establishConnection() throws SQLException {
        String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
}
