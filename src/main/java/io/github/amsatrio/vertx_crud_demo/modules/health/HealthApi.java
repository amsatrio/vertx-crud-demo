package io.github.amsatrio.vertx_crud_demo.modules.health;

import io.github.amsatrio.vertx_crud_demo.dto.response.Response;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Singleton;

@Singleton
public class HealthApi {
    public void status(RoutingContext routingContext) {
        Response<String> response = Response.success(200, routingContext.normalizedPath(), "ok");

        routingContext.response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(response.getStatus()).end(response.toBuffer());
    }
}
