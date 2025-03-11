package com.example.sharedjavagemini;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

    import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

    @Component
    public class ReadAndUpdateTasklet implements Tasklet {

        private final DataSource targetDataSource;

        public ReadAndUpdateTasklet(DataSource targetDataSource) {
            this.targetDataSource = targetDataSource;
        }

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            try (Connection connection = targetDataSource.getConnection();
                 PreparedStatement selectStmt = connection.prepareStatement("SELECT id, column_value FROM target_table WHERE condition_column = ?");
                 PreparedStatement updateStmt = connection.prepareStatement("UPDATE target_table SET column_value = ? WHERE id = ?")) {

                // Example: Query rows matching some condition
                selectStmt.setString(1, "condition_value");
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String columnValue = rs.getString("column_value");

                        // Example update logic
                        updateStmt.setString(1, "updated_" + columnValue);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();

                        System.out.println("Updated row ID: " + id);
                    }
                }
            }

            return RepeatStatus.FINISHED;
        }
    }

