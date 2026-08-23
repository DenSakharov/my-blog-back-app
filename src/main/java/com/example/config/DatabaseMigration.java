package com.example.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigration {

    private final DataSource dataSource;

    public DatabaseMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load()
                .migrate();
    }
}