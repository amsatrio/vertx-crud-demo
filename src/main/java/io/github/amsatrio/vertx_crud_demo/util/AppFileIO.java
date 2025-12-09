package io.github.amsatrio.vertx_crud_demo.util;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class AppFileIO {
    public static Future<String> readFile(String path) {
        Promise<String> promise = Promise.promise();
        Vertx vertx = VertxSingletonHolder.vertx();
        vertx.fileSystem().readFile(path).onSuccess(buffer -> {
            promise.complete(buffer.toString());
        }).onFailure(promise::fail);

        return promise.future();
    }
}
