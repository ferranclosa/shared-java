package com.example.sharedjavagemini;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Component
public class DynamicTargetDatabaseTasklet implements Tasklet {

    private final DynamicDataSource dataSourceService;

    public DynamicTargetDatabaseTasklet(DynamicDataSource dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        // Example: API-supplied database parameters
        String url = "jdbc:oracle:thin:@your-oracle-db-server:1521:ORCL";
        String username = "your_user";
        String password = "your_password";
        String driverClassName = "oracle.jdbc.OracleDriver";

        // Dynamically create DataSource based on these parameters
        DataSource targetDataSource = dataSourceService.createDataSource(url, username, password, driverClassName);

        // Perform JDBC operations using the newly created DataSource
        try (Connection connection = targetDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE target_table SET column_value = ? WHERE id = ?")) {

            // Example update SQL
            statement.setString(1, "new_value");
            statement.setInt(2, 123); // Example row ID
            int rowsUpdated = statement.executeUpdate();

            System.out.println("Rows updated: " + rowsUpdated);
        }

        return RepeatStatus.FINISHED;
    }
}