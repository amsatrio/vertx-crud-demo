package io.github.amsatrio.vertx_crud_demo.config;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Singleton;

@Singleton
public class ApplicationConfig {
    private JsonObject jsonObject = new JsonObject();

    public JsonObject getJsonObject() {
        return jsonObject == null ? null : jsonObject.copy();
    }

    public Future<JsonObject> init() {
        Promise<JsonObject> promise = Promise.promise();
        getApplicationConfig(promise, jsonObject);
        return promise.future();
    }

    private Future<JsonObject> getApplicationConfig(Promise<JsonObject> promise, JsonObject jsonObject) {
        String fileFormat = "yaml"; // yaml, properties
        String filePath = "application." + fileFormat;
        if (!jsonObject.isEmpty()) {
            String environment = jsonObject.getJsonObject("application").getString("environment", "local");
            filePath = "application-" + environment + "." + fileFormat;
        }
        ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
                .setType("file")
                .setFormat(fileFormat)
                .setConfig(new JsonObject().put("path", filePath));

        ConfigRetriever configRetriever = ConfigRetriever.create(VertxSingletonHolder.vertx(),
                new ConfigRetrieverOptions().addStore(configStoreOptions));

        configRetriever.getConfig()
                .onFailure(exception -> {
                    promise.fail(exception);
                })
                .onSuccess(data -> {
                    if (jsonObject.isEmpty()) {
                        getApplicationConfig(promise, data);
                        return;
                    }
                    JsonObject mergedConfig = jsonObject.copy();
                    mergeConfigs(mergedConfig, data);
                    promise.complete(mergedConfig);

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
