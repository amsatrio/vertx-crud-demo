package io.github.amsatrio.vertx_crud_demo.modules.web;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.inject.Singleton;

@Singleton
public class AppWeb implements Handler<RoutingContext> {
    
    public StaticHandler staticHandler() {
        return StaticHandler.create(FileSystemAccess.RELATIVE, "webroot")
            .setCachingEnabled(true)
            .setMaxAgeSeconds(3600) // 1 hour cache
            .setIndexPage("index.html")
            .setFilesReadOnly(true)
            .setIncludeHidden(false)
            .setCacheEntryTimeout(30000) // 30 seconds
            .setMaxCacheSize(10000)
            .setEnableRangeSupport(true)
            .setSendVaryHeader(true)
            .setDefaultContentEncoding("UTF-8")
            .setAlwaysAsyncFS(false)
            .setEnableFSTuning(true);
    }

    public void htmlTemplateEngineHandle() {

    }

    @Override
    public void handle(RoutingContext routingContext) {

    }
}
