package com.raunlo.checklist.config;

import com.raunlo.checklist.persistence.TaskDao;
import com.raunlo.checklist.persistence.mapper.ChecklistMapper;
import com.raunlo.checklist.persistence.mapper.TaskMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.mapstruct.factory.Mappers;

import javax.sql.DataSource;

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
    public TaskDao taskDao(Jdbi jdbi) {
        return jdbi.onDemand(TaskDao.class);
    }

    @Produces
    @Singleton
    public TaskMapper taskMapper() {
        return Mappers.getMapper(TaskMapper.class);
    }

    @Produces
    @Singleton
    public ChecklistMapper checklistMapper() {
        return Mappers.getMapper(ChecklistMapper.class);
    }
}
