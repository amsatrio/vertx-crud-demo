package io.github.amsatrio.vertx_crud_demo.modules.hello_world;

import io.github.amsatrio.vertx_crud_demo.dto.exception.BadRequestException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.DataExistException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.DataNotFoundException;
import io.github.amsatrio.vertx_crud_demo.dto.response.Response;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.SchemaRepository;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class HelloWorldApi {
	private SchemaRepository schemaRepository = SchemaRepository
			.create(new JsonSchemaOptions().setBaseUri("https://vertx.io").setDraft(Draft.DRAFT7));

	public void init(Router router) {
		String baseApiUrl = "/v1/hello-world"; 
		router.get(baseApiUrl + "/hello").handler(this::hello);
		router.get(baseApiUrl + "/log").handler(this::helloLog);
		router.get(baseApiUrl + "/error").handler(this::helloError);
		router.get(baseApiUrl + "/:name")
				.handler(HelloWorld.validationPathParamNameHandler(schemaRepository))
				.handler(this::helloByName);
	}

	public void hello(RoutingContext routingContext) {
		Response<String> response = Response.success(200, routingContext.normalizedPath(), "hello world");

		routingContext.response()
				.putHeader("Content-Type", "application/json")
				.setStatusCode(response.getStatus()).end(response.toBuffer());
	}

	public void helloByName(RoutingContext routingContext) {

		String name = routingContext.pathParam("name");

		Response<String> response = Response.success(200, routingContext.normalizedPath(), "hello " + name);

		routingContext.response()
				.putHeader("Content-Type", "application/json")
				.setStatusCode(response.getStatus())
				.end(response.toBuffer());
	}

	public void helloLog(RoutingContext routingContext) {
		log.info("helloLog");
		String logLevel = routingContext.queryParams().get("logLevel");

		if (logLevel.equals("info")) {
			log.info("helloLog info");
		} else if (logLevel.equals("error")) {
			log.error("helloLog error");
		} else {
			log.debug("invalid log level");
		}

		Response<String> response = Response.success(200, routingContext.normalizedPath());
		routingContext.response()
				.putHeader("Content-Type", "application/json")
				.setStatusCode(response.getStatus())
				.end(response.toBuffer());
	}

	public void helloError(RoutingContext routingContext) {
		String errorType = routingContext.queryParams().get("errorType");
		if(errorType.equals("bad_request")){
			throw new BadRequestException("sample bad request");
		} else if(errorType.equals("data_exist")){
			throw new DataExistException("sample data exist");
		} else if(errorType.equals("data_not_found")){
			throw new DataNotFoundException("sample not found");
		}

		throw new RuntimeException("invalid error");
	}
}
