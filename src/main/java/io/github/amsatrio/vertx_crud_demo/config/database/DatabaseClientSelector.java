package io.github.amsatrio.vertx_crud_demo.config.database;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.sqlclient.SqlClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class DatabaseClientSelector {
    private final Map<DatabaseType, DatabaseClientProvider> databaseClients;

    @Inject
    public DatabaseClientSelector(Set<DatabaseClientProvider> providers) {
        this.databaseClients = providers.stream()
                .collect(Collectors.toMap(
                        provider -> {
                            if (provider instanceof MariaDBDatabaseConfig) {
                                return DatabaseType.MARIADB;
                            } else if (provider instanceof PostgreSQLDatabaseConfig) {
                                return DatabaseType.POSTGRESQL;
                            } else if (provider instanceof SQLServerDatabaseConfig) {
                                return DatabaseType.SQLSERVER;
                            }else if (provider instanceof JDBCH2DatabaseConfig) {
                                return DatabaseType.H2;
                            }
                            throw new IllegalArgumentException("Unknown database provider: " + provider.getClass());
                        },
                        Function.identity()));
    }

    public SqlClient getSqlClient(DatabaseType databaseType) {
        DatabaseClientProvider provider = databaseClients.get(databaseType);
        if (provider == null) {
            throw new IllegalArgumentException("No provider found for database type: " + databaseType);
        }
        return provider.getSqlClient();
    }
}