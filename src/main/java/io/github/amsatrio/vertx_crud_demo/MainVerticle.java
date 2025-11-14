package io.github.amsatrio.vertx_crud_demo;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.github.amsatrio.vertx_crud_demo.config.LoggerConfig;
import io.github.amsatrio.vertx_crud_demo.modules.health.HealthApi;
import io.github.amsatrio.vertx_crud_demo.modules.hello_world.HelloWorldApi;
import io.micronaut.context.BeanContext;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends VerticleBase {
  private BeanContext beanContext;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);

    // init logger
    System.setProperty("vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.SLF4JLogDelegateFactory");
    DatabindCodec.mapper().registerModule(new JavaTimeModule());

    // init bean
    beanContext = BeanContext.run();
  }

  @Override
  public Future<?> start() {
    VertxSingletonHolder.setVertx(vertx);

    LoggerConfig loggerConfig = beanContext.getBean(LoggerConfig.class);
    ApplicationConfig applicationConfig = beanContext.getBean(ApplicationConfig.class);
    return applicationConfig.init()
        .onFailure(exception -> {
          log.error("get application config failed:", exception);
        })
        .compose(result -> {
          loggerConfig.init(result);
          log.debug(result.encodePrettily());
          return startHttpServer(result);
        });
  }

  private Future<HttpServer> startHttpServer(JsonObject jsonObject) {
    JsonObject serverJsonObject = jsonObject.getJsonObject("server");
    JsonObject tlsJsonObject = serverJsonObject.getJsonObject("tls");

    HttpServerOptions options = new HttpServerOptions()
        .setSsl(tlsJsonObject.getBoolean("enabled"))
        .setKeyCertOptions(new PemKeyCertOptions()
            .setKeyPath(tlsJsonObject.getString("key_file"))
            .setCertPath(tlsJsonObject.getString("cert_file")))
        .setCompressionSupported(serverJsonObject.getBoolean("compression"));

    return vertx.createHttpServer(options)
        .requestHandler(router())
        .listen(serverJsonObject.getInteger("port"), serverJsonObject.getString("host"))
        .onSuccess(server -> {
          log.info("HTTP server started on port " + serverJsonObject.getInteger("port"));
        });
  }

  private Router router() {
    Router router = Router.router(vertx);

    HealthApi healthApi = beanContext.getBean(HealthApi.class);
    router.get("/v1/health/status").handler(healthApi::status);

    HelloWorldApi helloWorldApi = beanContext.getBean(HelloWorldApi.class);
    router.get("/v1/hello-world/hello").handler(helloWorldApi::hello);

    return router;
  }

}
