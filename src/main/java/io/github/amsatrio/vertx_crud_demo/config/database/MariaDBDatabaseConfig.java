package io.github.amsatrio.vertx_crud_demo.config.database;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MariaDBDatabaseConfig implements DatabaseClientProvider {

    private SqlClient sqlClient;
    private final JsonObject optionJsonObject;
    private final JsonObject poolJsonObject;

    @Inject
    public MariaDBDatabaseConfig(ApplicationConfig applicationConfig) {
        optionJsonObject = applicationConfig.getJsonObject().getJsonObject("database").getJsonObject("mariadb");
        poolJsonObject =  optionJsonObject.getJsonObject("pool");

        init();
    }

    private void init() {
        log.debug("MySQL Database Init()");

        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(optionJsonObject.getInteger("port"))
                .setHost(optionJsonObject.getString("host"))
                .setDatabase(optionJsonObject.getString("db_name"))
                .setUser(optionJsonObject.getString("username"))
                .setPassword(optionJsonObject.getString("password"));

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(poolJsonObject.getInteger("max"));

        // Create the client pool
        sqlClient = MySQLBuilder
                .client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(VertxSingletonHolder.vertx())
                .build();
    }

    @Override
    public SqlClient getSqlClient() {
        return this.sqlClient;
    }
}