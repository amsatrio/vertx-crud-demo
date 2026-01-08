package io.github.amsatrio.vertx_crud_demo;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.github.amsatrio.vertx_crud_demo.config.CorsConfig;
import io.github.amsatrio.vertx_crud_demo.config.LoggerConfig;
import io.github.amsatrio.vertx_crud_demo.handler.exception.ApiExceptionHandler;
import io.github.amsatrio.vertx_crud_demo.handler.middleware.LoggerMiddleware;
import io.github.amsatrio.vertx_crud_demo.modules.health.HealthApi;
import io.github.amsatrio.vertx_crud_demo.modules.hello_world.HelloWorldApi;
import io.github.amsatrio.vertx_crud_demo.modules.m_biodata.MBiodataApi;
import io.github.amsatrio.vertx_crud_demo.modules.web.AppWeb;
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
import io.vertx.ext.web.handler.BodyHandler;
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
          applicationConfig.setJsonObject(result);
          loggerConfig.init(result);
          // log.info("=== env config");
          // log.info(result.encodePrettily());
          return startHttpServer(result);
        });
  }

  private Future<HttpServer> startHttpServer(JsonObject jsonObject) {
    JsonObject serverJsonObject = jsonObject.getJsonObject("server");
    JsonObject tlsJsonObject = serverJsonObject.getJsonObject("tls");
    JsonObject compressionJsonObject = serverJsonObject.getJsonObject("compression");

    HttpServerOptions options = new HttpServerOptions()
        .setSsl(tlsJsonObject.getBoolean("enabled"))
        .setKeyCertOptions(new PemKeyCertOptions()
            .setKeyPath(tlsJsonObject.getString("key-file"))
            .setCertPath(tlsJsonObject.getString("cert-file")))
        .setCompressionSupported(compressionJsonObject.getBoolean("enabled"))
        .setCompressionContentSizeThreshold(compressionJsonObject.getInteger("min-response-size"));

    return vertx.createHttpServer(options)
        .requestHandler(router())
        .listen(serverJsonObject.getInteger("port"), serverJsonObject.getString("host"))
        .onSuccess(server -> {
          log.info("HTTP server started on " + serverJsonObject.getString("host") + ":" + serverJsonObject.getInteger("port"));
        });
  }

  private Router router() {
    Router router = Router.router(vertx);

    // PAYLOAD HANDLER
    BodyHandler bodyHandler = BodyHandler.create()
        .setDeleteUploadedFilesOnEnd(true)
        .setUploadsDirectory("tmp/file-uploads")
        .setBodyLimit(100 * 1024 * 1024); // set 100MB limit

    // INIT MIDDLEWARE HANDLER
    LoggerMiddleware loggerMiddleware = beanContext.getBean(LoggerMiddleware.class);

    // CORS
    CorsConfig corsConfig = this.beanContext.getBean(CorsConfig.class);

    router.route()
        .handler(corsConfig.getCorsHandler())
        .handler(bodyHandler)
        .handler(loggerMiddleware);

    HealthApi healthApi = beanContext.getBean(HealthApi.class);
    healthApi.init(router);

    HelloWorldApi helloWorldApi = beanContext.getBean(HelloWorldApi.class);
    helloWorldApi.init(router);

    // INIT EXCEPTION HANDLER
    ApiExceptionHandler apiExceptionHandler = beanContext.getBean(ApiExceptionHandler.class);

    MBiodataApi mBiodataApi = beanContext.getBean(MBiodataApi.class);
    mBiodataApi.init(router);

    // STATIC
    AppWeb appWeb = beanContext.getBean(AppWeb.class);
    router.route("/public/*").handler(appWeb.staticHandler());

    // ERROR
    router.errorHandler(400, apiExceptionHandler::badRequest);
    router.errorHandler(405, apiExceptionHandler::invalidMethod);
    router.errorHandler(413, apiExceptionHandler::tooLarge);
    router.errorHandler(404, apiExceptionHandler::notFound);
    router.errorHandler(500, apiExceptionHandler::internalServerError);
    return router;
  }

}
