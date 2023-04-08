package com.raunlo.checklist.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;


@Readiness
@ApplicationScoped
@Slf4j
public class DatabaseHealthCheck implements HealthCheck {
    private final DataSource dataSource;

    @Inject()
    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Database up")
                .status(isDatabaseAccepting())
                .build();
    }

    private boolean isDatabaseAccepting() {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT 1 from task")) {
                return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
