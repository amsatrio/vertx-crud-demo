package io.github.amsatrio.vertx_crud_demo.handler.response;

import io.github.amsatrio.vertx_crud_demo.dto.response.Response;
import io.github.amsatrio.vertx_crud_demo.util.AppGenerator;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

public class ApiResponseHandler {

    public <T> void success(RoutingContext routingContext, T data) {
        Response<T> response = Response.success(200, routingContext.normalizedPath(), data);

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.put(AppGenerator.generateCacheKey(routingContext), response);
        routingContext.json(response);
    }

    public void error(RoutingContext routingContext, int status, String message) {
        Response<Object> response = Response.error(status, message, routingContext.normalizedPath());

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.put(AppGenerator.generateCacheKey(routingContext), response);
        routingContext.json(response);
    }

        public void success(RoutingContext routingContext) {
        Response<Object> response = Response.success(200, routingContext.normalizedPath());

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.put(AppGenerator.generateCacheKey(routingContext), response);
        routingContext.json(response);
    }

    public void file(RoutingContext routingContext, String filename, Buffer buffer) {
        Response<Object> response = Response.success(200, routingContext.normalizedPath());

        routingContext.response()
                .putHeader("Content-Disposition", "attachment; filename = " + filename)
                .putHeader("content-type", "application/octet-stream")
                .putHeader("content-length", String.valueOf(buffer.length()))
                .setStatusCode(response.getStatus());
                
        routingContext.end(buffer);
    }

}
