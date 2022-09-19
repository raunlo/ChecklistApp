package com.raunlo.checklist.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Liveness
@ApplicationScoped
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
             return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
