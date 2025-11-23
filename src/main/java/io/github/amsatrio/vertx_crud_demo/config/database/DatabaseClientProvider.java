package io.github.amsatrio.vertx_crud_demo.config.database;

import io.vertx.sqlclient.SqlClient;

public interface DatabaseClientProvider {
    SqlClient getSqlClient();
}
