package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "db_config.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILE));
            url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/db");
            user = properties.getProperty("db.user", "root");
            password = properties.getProperty("db.password", "root");
        } catch (IOException e) {
            System.err.println("Failed to load DB configuration: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}