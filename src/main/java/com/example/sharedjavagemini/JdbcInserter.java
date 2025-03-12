package com.example.sharedjavagemini;


import java.sql.*;

public class JdbcInserter {

    public void insertData(Connection sourceConnection, Connection targetConnection,
                           String sourceQuery, String insertSQL) throws SQLException {

        // Prepare the SELECT statement from the source database
        try (Statement sourceStatement = sourceConnection.createStatement();
             ResultSet resultSet = sourceStatement.executeQuery(sourceQuery);
             PreparedStatement insertStatement = targetConnection.prepareStatement(insertSQL)) {

            // Get Metadata to dynamically extract column data
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Loop through the ResultSet and populate the placeholders
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    // Dynamically extract column value and set in PreparedStatement
                    Object value = resultSet.getObject(i); // Generic data extraction
                    insertStatement.setObject(i, value);  // Set the placeholder
                }

                // Add to batch to optimize insert performance
                insertStatement.addBatch();
            }

            // Execute the batch
            int[] resultCounts = insertStatement.executeBatch();
            System.out.println("Inserted " + resultCounts.length + " rows successfully.");
        }
    }

    public static void main(String[] args) {
        String sourceQuery = "SELECT id, name, salary FROM source_table";
        String insertSQL = "INSERT INTO target_table (id, name, salary) VALUES (?, ?, ?)";

        try (Connection sourceConnection = DriverManager.getConnection("jdbc:oracle:thin:@source_host:1521:XE", "source_user", "source_password");
             Connection targetConnection = DriverManager.getConnection("jdbc:oracle:thin:@target_host:1521:XE", "target_user", "target_password")) {

            JdbcInserter helper = new JdbcInserter();
            helper.insertData(sourceConnection, targetConnection, sourceQuery, insertSQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}