package com.example.sharedjavagemini;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TableCloner {

    public static void cloneTable(Connection connection, String schemaName, String originalTableName, String clonedTableName) throws Exception {
        StringBuilder createTableDDL = new StringBuilder();

        // Get database metadata
        DatabaseMetaData metaData = connection.getMetaData();

        // Fetch column details
        try (ResultSet columns = metaData.getColumns(null, schemaName, originalTableName, "%")) {
            createTableDDL.append("CREATE TABLE ").append(clonedTableName).append(" (\n");

            boolean firstColumn = true; // To handle column commas in DDL
            while (columns.next()) {
                if (!firstColumn) {
                    createTableDDL.append(",\n");
                }

                // Column details
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                int decimalDigits = columns.getInt("DECIMAL_DIGITS");
                int nullable = columns.getInt("NULLABLE");

                // Append column definition to DDL
                createTableDDL.append("    ").append(columnName).append(" ").append(dataType);

                // Add size/scale information if applicable (e.g., for VARCHAR, NUMBER)
                if ("VARCHAR".equalsIgnoreCase(dataType) || "CHAR".equalsIgnoreCase(dataType)) {
                    createTableDDL.append("(").append(columnSize).append(")");
                } else if ("NUMBER".equalsIgnoreCase(dataType) || "DECIMAL".equalsIgnoreCase(dataType)) {
                    createTableDDL.append("(").append(columnSize).append(",").append(decimalDigits).append(")");
                }

                // Add NULL/NOT NULL constraint
                if (nullable == DatabaseMetaData.columnNullable) {
                    createTableDDL.append(" NULL");
                } else {
                    createTableDDL.append(" NOT NULL");
                }

                firstColumn = false;
            }
            createTableDDL.append("\n);");
        }

        // Primary keys (optional)
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, schemaName, originalTableName)) {
            StringBuilder primaryKeyDDL = new StringBuilder();
            boolean hasPrimaryKey = false;

            while (primaryKeys.next()) {
                if (!hasPrimaryKey) {
                    primaryKeyDDL.append(",\n    PRIMARY KEY (");
                    hasPrimaryKey = true;
                } else {
                    primaryKeyDDL.append(", ");
                }
                primaryKeyDDL.append(primaryKeys.getString("COLUMN_NAME"));
            }

            if (hasPrimaryKey) {
                primaryKeyDDL.append(")");
                createTableDDL.insert(createTableDDL.lastIndexOf(")"), primaryKeyDDL); // Append PRIMARY KEY after column list
            }
        }

        // Output generated DDL for debugging
        System.out.println("Generated DDL:");
        System.out.println(createTableDDL);

        // Execute the DDL to create the cloned table
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableDDL.toString());
        }

        System.out.println("Table " + clonedTableName + " created successfully!");
    }

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe"; // Replace with your DB URL
        String username = "your_username"; // Replace with your username
        String password = "your_password"; // Replace with your password
        String schemaName = "YOUR_SCHEMA"; // Replace with your schema name
        String originalTable = "YOUR_TABLE"; // Replace with the original table you want to clone
        String clonedTable = "YOUR_CLONED_TABLE"; // Replace with the name of the cloned table

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            cloneTable(connection, schemaName, originalTable, clonedTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}