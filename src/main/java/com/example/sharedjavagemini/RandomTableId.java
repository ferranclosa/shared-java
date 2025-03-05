package com.example.sharedjavagemini;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class RandomTableId {

    public static int getRandomId(Connection connection, String tableName) throws SQLException {
        if (connection == null || tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("Connection and tableName must not be null or empty.");
        }

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {

            if (resultSet.next()) {
                int tableSize = resultSet.getInt(1);
                if (tableSize > 0) {
                    Random random = new Random();
                    return random.nextInt(tableSize) + 1; // Generates a random number between 1 and tableSize (inclusive)
                } else {
                    throw new IllegalStateException("Table " + tableName + " is empty.");
                }
            } else {
                throw new SQLException("Could not retrieve table size.");
            }
        }
    }

    public static void main(String[] args) {
        // Example usage (replace with your database connection logic)
        String tableName = "your_table_name"; // Replace with your table name
        try (Connection connection = DatabaseConnection.getConnection()) { // Replace DatabaseConnection.getConnection() with your connection method
            int randomId = getRandomId(connection, tableName);
            System.out.println("Random ID: " + randomId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Example Database Connection class (replace with your connection logic)
class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        // Replace with your database connection details
        // Example with H2 in-memory database
        return java.sql.DriverManager.getConnection("jdbc:h2:mem:testdb", "user", "password");
    }
}