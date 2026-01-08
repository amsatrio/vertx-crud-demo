package io.github.amsatrio.vertx_crud_demo.config.database;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class JDBCH2DatabaseConfig implements DatabaseClientProvider {

    private SqlClient sqlClient;
    private final JsonObject optionJsonObject;
    private final JsonObject poolJsonObject;

    @Inject
    public JDBCH2DatabaseConfig(ApplicationConfig applicationConfig) {
        optionJsonObject = applicationConfig.getJsonObject().getJsonObject("database").getJsonObject("h2");
        poolJsonObject =  optionJsonObject.getJsonObject("pool");

        init();
    }

    private void init() {
        log.debug("JDBC H2 Database init()");

        JDBCConnectOptions connectOptions = new JDBCConnectOptions()
                .setJdbcUrl(optionJsonObject.getString("url"))
                .setUser(optionJsonObject.getString("username"))
                .setPassword(optionJsonObject.getString("password"));

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(poolJsonObject.getInteger("max"));

        // Create the client pool
        sqlClient = JDBCPool.pool(VertxSingletonHolder.vertx(), connectOptions, poolOptions);
    }

    @Override
    public SqlClient getSqlClient() {
        return this.sqlClient;
    }
}
