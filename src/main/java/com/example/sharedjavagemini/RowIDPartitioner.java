package com.example.sharedjavagemini;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class RowIdPartitioner implements Partitioner {

    private final String tableName;    // Table name
    private final String whereClause; // Your filtering condition
    private final int partitionCount; // Number of partitions

    public RowIdPartitioner(String tableName, String whereClause, int partitionCount) {
        this.tableName = tableName;
        this.whereClause = whereClause;
        this.partitionCount = partitionCount;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();

        String query = String.format(
                "SELECT MIN(ROWID) AS min_rowid, MAX(ROWID) AS max_rowid FROM %s %s",
                tableName, whereClause.isEmpty() ? "" : "WHERE " + whereClause
        );

        try (Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                String minRowId = resultSet.getString("min_rowid");
                String maxRowId = resultSet.getString("max_rowid");

                // Divide range into partitions
                for (int i = 0; i < partitionCount; i++) {
                    ExecutionContext context = new ExecutionContext();

                    // Calculate ROWID boundaries for this partition
                    String startRowId = calculatePartitionBoundary(minRowId, maxRowId, i, partitionCount, true);
                    String endRowId = calculatePartitionBoundary(minRowId, maxRowId, i, partitionCount, false);

                    // Populate ExecutionContext
                    context.putString("startRowId", startRowId);
                    context.putString("endRowId", endRowId);
                    context.putInt("partitionIndex", i);

                    partitions.put("partition" + i, context);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate ROWID-based partitions", e);
        }

        return partitions;
    }

    /**
     * Calculates a ROWID boundary for the given partition index.
     */
    private String calculatePartitionBoundary(String minRowId, String maxRowId, int partitionIndex, int partitionCount, boolean isStart) {
        // For simplicity, returning exact slices of the table as even chunks
        // Advanced logic can compute ranges dynamically using block-level analysis
        if (isStart) {
            return partitionIndex == 0 ? minRowId : minRowId + "+" + partitionIndex; // Adjust if using DBMS_ROWID.SPLIT
        } else {
            return partitionIndex == partitionCount - 1 ? maxRowId : minRowId + "+" + (partitionIndex + 1);
        }
    }
}
