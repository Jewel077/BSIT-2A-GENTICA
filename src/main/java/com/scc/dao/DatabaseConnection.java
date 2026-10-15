package com.scc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:movietickets.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Creates the users table (if missing) and a default admin account
    public static void initializeDatabase() {
        String createUsers = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        String seedAdmin = "INSERT OR IGNORE INTO users(username, password, roles, status) "
                + "VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(seedAdmin);
        } catch (SQLException e) {
            System.err.println("Database setup failed: " + e.getMessage());
        }
    }
}
