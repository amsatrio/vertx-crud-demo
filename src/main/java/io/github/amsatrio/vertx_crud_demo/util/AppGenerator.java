package io.github.amsatrio.vertx_crud_demo.util;

import java.util.UUID;

import io.vertx.ext.web.RoutingContext;

public class AppGenerator {
    public static String generateCacheKey(RoutingContext routingContext) {
        return routingContext.request().method() + ":" +
                routingContext.request().path() +
                (routingContext.request().query() != null ? "?" + routingContext.request().query() : "");
    }

    public static String generateUUID(){
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId;
    }
}
