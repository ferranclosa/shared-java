package com.example.sharedjavagemini;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RowIdProcessingTasklet implements Tasklet {

    private final String tableName;

    public RowIdProcessingTasklet(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public RepeatStatus execute(org.springframework.batch.core.StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        // Get partition-specific execution context
        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
        String startRowId = executionContext.getString("startRowId");
        String endRowId = executionContext.getString("endRowId");

        try (Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "username", "password");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT ROWID, column1, column2 FROM " + tableName +
                             " WHERE ROWID BETWEEN ? AND ?"
             )) {

            statement.setString(1, startRowId);
            statement.setString(2, endRowId);

            ResultSet resultSet = statement.executeQuery();

            // Process each row
            while (resultSet.next()) {
                String rowId = resultSet.getString("ROWID");
                String column1 = resultSet.getString("column1");
                String column2 = resultSet.getString("column2");

                System.out.printf("Processing ROWID: %s, column1: %s, column2: %s%n", rowId, column1, column2);
            }
        }

        return RepeatStatus.FINISHED;
    }
}