package io.github.amsatrio.vertx_crud_demo;

import java.util.Objects;

import io.vertx.core.Vertx;

public class VertxSingletonHolder {
    private static Vertx vertxInstance;

    private VertxSingletonHolder() {
    }

    public static Vertx vertx() {
        return Objects.requireNonNull(vertxInstance, "Vertx instance must not be null");
    }

    protected static synchronized void setVertx(Vertx vertx) {
        if (vertxInstance == null) {
            vertxInstance = vertx;
        } else {
            throw new IllegalStateException("Vertx instance is already set.");
        }
    }

    protected static synchronized void closeVertx() {
        if (vertxInstance != null) {
            vertxInstance.close();
            vertxInstance = null;
        }
    }
}
