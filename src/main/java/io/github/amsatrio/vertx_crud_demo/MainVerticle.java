package io.github.amsatrio.vertx_crud_demo;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    Promise<JsonObject> applicationConfigPromise = Promise.promise();
    Future<JsonObject> applicationConfig = getApplicationConfig(applicationConfigPromise, new JsonObject());
    return applicationConfig
        .onFailure(exception -> {
          exception.printStackTrace();
        })
        .compose(data -> {
          System.out.println(data.encodePrettily());
          return vertx.createHttpServer().requestHandler(req -> {
            req.response()
                .putHeader("content-type", "text/plain")
                .end("Hello from Vert.x!");
          }).listen(data.getJsonObject("server").getInteger("port")).onSuccess(http -> {
            System.out.println("HTTP server started on port 8888");
          });

        });
  }

  private Future<JsonObject> getApplicationConfig(Promise<JsonObject> promise, JsonObject jsonObject) {
    String filePath = "application.yaml";
    if (!jsonObject.isEmpty()) {
      String environment = jsonObject.getJsonObject("application").getString("environment", "local");
      filePath = "application-" + environment + ".yaml";
    }
    ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
        .setType("file")
        .setFormat("yaml")
        .setConfig(new JsonObject().put("path", filePath));

    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
        new ConfigRetrieverOptions().addStore(configStoreOptions));

    configRetriever.getConfig()
        .onFailure(exception -> {
          promise.fail(exception);
        })
        .onSuccess(data -> {
          if (jsonObject.isEmpty()) {
            getApplicationConfig(promise, data);
          } else {
            JsonObject mergedConfig = jsonObject.copy();
            mergeConfigs(mergedConfig, data);
            promise.complete(mergedConfig);
          }
        });

    return promise.future();
  }

  private void mergeConfigs(JsonObject target, JsonObject source) {
    for (String key : source.fieldNames()) {
      if (source.getValue(key) instanceof JsonObject && target.containsKey(key)) {
        mergeConfigs(target.getJsonObject(key), source.getJsonObject(key));
      } else {
        target.put(key, source.getValue(key));
      }
    }
  }
}
