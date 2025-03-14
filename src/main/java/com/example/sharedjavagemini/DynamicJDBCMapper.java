package com.example.sharedjavagemini;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class DynamicJDBCMapper {

    public static Map<String, Object> mapRow(ResultSet resultSet) throws SQLException {
        // Result object to hold column-value mappings
        Map<String, Object> row = new HashMap<>();

        // Get metadata for the ResultSet
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Iterate through columns and extract data dynamically
        for (int col = 1; col <= columnCount; col++) {
            String columnName = metaData.getColumnName(col); // Column name
            int columnType = metaData.getColumnType(col);    // JDBC Type
            int precision = metaData.getPrecision(col);      // Precision
            int scale = metaData.getScale(col);              // Scale (for decimal numbers)

            Object value = getColumnValue(resultSet, col, columnType, precision, scale);
            row.put(columnName, value);
        }

        return row;
    }

    private static Object getColumnValue(ResultSet resultSet, int columnIndex, int columnType, int precision, int scale) throws SQLException {
        switch (columnType) {
            case Types.INTEGER:
                return resultSet.getObject(columnIndex) == null ? null : resultSet.getInt(columnIndex); // Integer values

            case Types.BIGINT:
                return resultSet.getObject(columnIndex) == null ? null : resultSet.getLong(columnIndex); // Long values

            case Types.SMALLINT:
            case Types.TINYINT:
                return resultSet.getObject(columnIndex) == null ? null : resultSet.getShort(columnIndex); // Short values

            case Types.NUMERIC:
            case Types.DECIMAL: // Use BigDecimal for precise values
                BigDecimal decimalValue = resultSet.getBigDecimal(columnIndex);
                if (decimalValue != null && scale > 0) {
                    decimalValue = decimalValue.setScale(scale); // Apply scale
                }
                return decimalValue;

            case Types.FLOAT:
            case Types.REAL:
                return resultSet.getObject(columnIndex) == null ? null : resultSet.getFloat(columnIndex);

            case Types.DOUBLE:
                return resultSet.getObject(columnIndex) == null ? null : resultSet.getDouble(columnIndex);

            case Types.BOOLEAN: // Boolean values
                return resultSet.getObject(columnIndex) == null ? null : resultSet.getBoolean(columnIndex);

            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR: // Strings
                return resultSet.getString(columnIndex);

            case Types.DATE: // Date
                java.sql.Date date = resultSet.getDate(columnIndex);
                return (date != null) ? date.toLocalDate() : null; // Convert to LocalDate if needed

            case Types.TIME: // Time
                java.sql.Time time = resultSet.getTime(columnIndex);
                return (time != null) ? time.toLocalTime() : null; // Convert to LocalTime if needed

            case Types.TIMESTAMP: // Timestamp
                java.sql.Timestamp timestamp = resultSet.getTimestamp(columnIndex);
                return (timestamp != null) ? timestamp.toLocalDateTime() : null; // Convert to LocalDateTime

            case Types.TIMESTAMP_WITH_TIMEZONE: // Handle timestamp with timezone
                return resultSet.getObject(columnIndex, OffsetDateTime.class);

            case Types.BLOB: // Binary Data
                Blob blob = resultSet.getBlob(columnIndex);
                return (blob != null) ? blob.getBytes(1, (int) blob.length()) : null;

            case Types.CLOB: // Large Text
                Clob clob = resultSet.getClob(columnIndex);
                return (clob != null) ? clob.getSubString(1, (int) clob.length()) : null;

            case Types.VARBINARY:
            case Types.BINARY: // Binary Arrays
                return resultSet.getBytes(columnIndex);

            case Types.ARRAY: // SQL ARRAY
                Array array = resultSet.getArray(columnIndex);
                return (array != null) ? array.getArray() : null;

            case Types.ROWID: // Row ID
                RowId rowId = resultSet.getRowId(columnIndex);
                return (rowId != null) ? rowId.toString() : null;

            default:
                // For unhandled types, return as string format
                return resultSet.getObject(columnIndex);
        }
    }

    public static void main(String[] args) {
        // Example JDBC query with Oracle
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "your_username";
        String password = "your_password";

        String query = "SELECT * FROM your_table"; // Replace with your table or query

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iterate through rows and map results dynamically
            while (resultSet.next()) {
                Map<String, Object> row = mapRow(resultSet);
                row.forEach((columnName, value) -> {
                    System.out.println(columnName + " = " + value);
                });
                System.out.println("------");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}