package com.raunlo.checklist.testcontainers;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresSQLTestContainer implements
    QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {
  private Optional<String> networkId;
  static PostgreSQLContainer<?> db =
      new PostgreSQLContainer<>("postgres:14")
          .withDatabaseName("postgres")
          .withUsername("postgres")
          .withPassword("postgres")
          .withClasspathResourceMapping("init.sql",
              "/docker-entrypoint-initdb.d/init.sql",
              BindMode.READ_ONLY);

  @Override
  public Map<String, String> start() {
    networkId.ifPresent(db::withNetworkMode);

    db.start();

    String jdbcUrl = db.getJdbcUrl();
    if (this.networkId.isPresent()) {
      // Replace hostname + port in the provided JDBC URL with the hostname of the Docker container
      // running PostgreSQL and the listening port.
      jdbcUrl = fixJdbcUrl(jdbcUrl);
    }

    return Collections.singletonMap(
        "JDBC_DATABASE_URL", jdbcUrl
    );
  }

  @Override
  public void stop() {
    db.stop();
  }

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    this.networkId = context.containerNetworkId();
  }

  private String fixJdbcUrl(String jdbcUrl) {
    // Part of the JDBC URL to replace
    String hostPort = db.getHost() + ':' + db.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);

    // Host/IP on the container network plus the unmapped port
    String networkHostPort =
        db.getCurrentContainerInfo().getConfig().getHostName()
            + ':'
            + PostgreSQLContainer.POSTGRESQL_PORT;

    return jdbcUrl.replace(hostPort, networkHostPort);
  }
}
