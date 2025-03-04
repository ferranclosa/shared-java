package com.example.sharedjavagemini;
import java.sql.*;

public class GetPrimaryKeys {

    public static void main(String[] args) {
        String url = "jdbc:your_database_url";
        String user = "your_username";
        String password = "your_password";
        String tableName = "your_table_name";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

            System.out.println("Primary key columns for table " + tableName + ":");
            while (primaryKeys.next()) {
                String columnName = primaryKeys.getString("COLUMN_NAME");
                System.out.println(columnName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
