package college.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnections {
    private static String environmentOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            throw new SQLException("Set the DB_PASSWORD environment variable before starting the application.");
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found. Add the driver JAR to lib/.", e);
        }
        String url = environmentOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/college_management");
        String user = environmentOrDefault("DB_USER", "postgres");
        return DriverManager.getConnection(url, user, password);
    }
}
