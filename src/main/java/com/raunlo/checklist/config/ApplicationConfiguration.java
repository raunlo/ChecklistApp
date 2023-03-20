package com.raunlo.checklist.config;

import com.raunlo.checklist.persistence.dao.PostgreChecklistItemDao;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import javax.sql.DataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@Dependent
public class ApplicationConfiguration {

    @Produces
    public DataSource dataSource(@ConfigProperty(name ="JDBC_DATABASE_USER") String username,
                                 @ConfigProperty(name = "JDBC_DATABASE_PASSWORD") String password,
                                 @ConfigProperty(name = "JDBC_DATABASE_URL") String dbUrl,
                                 @ConfigProperty(name = "JDBC_DATABASE_CLASS_NAME") String driverClassName) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setAutoCommit(true);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");

        return new HikariDataSource(config);
    }

    @Produces
    public Jdbi jdbi(DataSource dataSource) {
        final Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }

    @Produces
    public PostgreChecklistItemDao taskDao(Jdbi jdbi) {
        return jdbi.onDemand(PostgreChecklistItemDao.class);
    }

    @Produces
    public com.raunlo.baseItemList.persistence.dao.ChecklistDao checklistDao(Jdbi jdbi) {return jdbi.onDemand(
      com.raunlo.baseItemList.persistence.dao.ChecklistDao.class);    }
}
