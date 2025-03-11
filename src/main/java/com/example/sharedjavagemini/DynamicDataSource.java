package com.example.sharedjavagemini;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DynamicDataSource {

    public DataSource createDataSource(String url, String username, String password, String driverClassName) {
        // Use HikariCP or any other pool library to build DataSource
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        // Optional: Configure connection pool settings
        dataSource.setMinimumIdle(3);
        dataSource.setMaximumPoolSize(10);
        dataSource.setIdleTimeout(30000);
        dataSource.setConnectionTimeout(30000);
        dataSource.setValidationTimeout(5000);
        // RDBMS-specific test query
        if (driverClassName.contains("oracle")) {
            dataSource.setConnectionTestQuery("SELECT 1 FROM DUAL");
        } else if (driverClassName.contains("postgresql")) {
            dataSource.setConnectionTestQuery("SELECT 1");
        } else if (driverClassName.contains("db2")) {
            dataSource.setConnectionTestQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");
        }

        return dataSource;
    }
}