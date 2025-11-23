package io.github.amsatrio.vertx_crud_demo.modules.hello_world;

import io.github.amsatrio.vertx_crud_demo.dto.response.Response;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Singleton;

@Singleton
public class HelloWorldApi {
	public void init(Router router) {
		router.get("/v1/hello-world/hello").handler(this::hello);
	}

	public void hello(RoutingContext routingContext) {
		Response<String> response = Response.success(200, routingContext.normalizedPath(), "hello world");

		routingContext.response()
				.putHeader("Content-Type", "application/json")
				.setStatusCode(response.getStatus()).end(response.toBuffer());
	}
}
