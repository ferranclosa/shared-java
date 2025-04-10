package com.example.sharedjavagemini;

import java.sql.Clob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
public class PopulateSQL {
    public static String populateSQL(String sql, Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return sql; // No parameters, return as is
        }

        for (Object param : parameters) {
            String replacement;

            if (param == null) {
                // Handle NULL values
                replacement = "NULL";
            } else if (param instanceof String) {
                // Handle String and escape single quotes
                replacement = "'" + param.toString().replace("'", "''") + "'";
            } else if (param instanceof Integer || param instanceof Long || param instanceof Short || param instanceof Byte) {
                // Handle integer types
                replacement = param.toString();
            } else if (param instanceof Double || param instanceof Float) {
                // Handle floating-point types
                replacement = param.toString();
            } else if (param instanceof Boolean) {
                // Handle booleans (convert true -> 1, false -> 0 for SQL)
                replacement = ((Boolean) param) ? "1" : "0";
            } else if (param instanceof Timestamp) {
                // Handle Timestamp (format as SQL TIMESTAMP)
                Timestamp timestamp = (Timestamp) param;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                replacement = "TO_TIMESTAMP('" + sdf.format(timestamp) + "', 'YYYY-MM-DD HH24:MI:SS')";
            } else if (param instanceof java.sql.Date) {
                // Handle java.sql.Date (format as SQL DATE)
                java.sql.Date date = (java.sql.Date) param;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                replacement = "TO_DATE('" + sdf.format(date) + "', 'YYYY-MM-DD')";
            } else if (param instanceof java.util.Date) {
                // Handle java.util.Date (use TIMESTAMP conversion)
                java.util.Date date = (java.util.Date) param;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                replacement = "TO_TIMESTAMP('" + sdf.format(date) + "', 'YYYY-MM-DD HH24:MI:SS')";
            } else if (param instanceof Clob) {
                // Handle CLOB explicitly (convert to string if small enough)
                Clob clob = (Clob) param;
                try {
                    // Use Clob.getSubString(), assuming it's not too large
                    replacement = "'" + clob.getSubString(1, (int) clob.length()).replace("'", "''") + "'";
                } catch (Exception e) {
                    throw new RuntimeException("Error reading Clob value.", e);
                }
            } else {
                // Fallback: Use toString() for unknown types
                replacement = "'" + param.toString().replace("'", "''") + "'";
            }

            // Replace the first '?' placeholder with the generated value
            sql = sql.replaceFirst("\\?", replacement);
        }

        return sql;
    }

    for (int i = 0; i < parameters.length; i++) {
        Object param = parameters[i];

        if (param == null) {
            pstmt.setNull(i + 1, java.sql.Types.NULL);
        } else if (param instanceof String) {
            pstmt.setString(i + 1, (String) param);
        } else if (param instanceof Integer) {
            pstmt.setInt(i + 1, (Integer) param);
        } else if (param instanceof Long) {
            pstmt.setLong(i + 1, (Long) param);
        } else if (param instanceof Double) {
            pstmt.setDouble(i + 1, (Double) param);
        } else if (param instanceof java.sql.Date) {
            pstmt.setDate(i + 1, (java.sql.Date) param);
        } else if (param instanceof java.sql.Timestamp) {
            pstmt.setTimestamp(i + 1, (java.sql.Timestamp) param);
        } else if (param instanceof Clob) {
            pstmt.setClob(i + 1, (Clob) param);
        } else if (param instanceof java.util.Date) {
            pstmt.setTimestamp(i + 1, new java.sql.Timestamp(((java.util.Date) param).getTime()));
        } else {
            // Fallback for unknown types
            pstmt.setObject(i + 1, param);
        }
    }
}