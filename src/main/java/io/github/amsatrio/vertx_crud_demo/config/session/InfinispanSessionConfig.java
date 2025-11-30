package io.github.amsatrio.vertx_crud_demo.config.session;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.configuration.ExhaustedAction;
import org.infinispan.configuration.cache.CacheMode;
import io.vertx.ext.web.sstore.infinispan.InfinispanSessionStore;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

@Slf4j
@Singleton
public class InfinispanSessionConfig implements SessionProvider {
    private final JsonObject sessionJsonObject;
    private final JsonObject infinispanJsonObject;

    @Inject
    public InfinispanSessionConfig(ApplicationConfig applicationConfig) {
        this.sessionJsonObject = applicationConfig.getJsonObject().getJsonObject("security").getJsonObject("session");
        this.infinispanJsonObject = applicationConfig.getJsonObject().getJsonObject("connection").getJsonObject("infinispan");
    }

    @Override
    public SessionHandler getSessionHandler() {
        log.info("getSessionHandler");

        JsonObject config = new JsonObject()
                .put("servers", new JsonArray()
                        .add(infinispanJsonObject))
                .put("cacheName", infinispanJsonObject.getString("cache_name"))
                .put("retryTimeout", infinispanJsonObject.getInteger("retry_timeout")); // ms

        RemoteCacheManager remoteCacheManager = createRemoteCacheManager();

        SessionStore sessionStore = InfinispanSessionStore.create(
                VertxSingletonHolder.vertx(),
                config,
                remoteCacheManager);
        return SessionHandler
                .create(sessionStore)
                .setCookieHttpOnlyFlag(true)
                .setCookieSecureFlag(sessionJsonObject.getBoolean("secure"))
                .setCookieMaxAge(30 * 24 * 60 * 60) // 30 days
                .setCookieless(false)
                .setCookieSameSite(null) // strict, lax, null
                .setLazySession(true)
                .setSessionCookieName(sessionJsonObject.getString("name"))
                .setSessionTimeout(sessionJsonObject.getInteger("timeout"));
    }

    public RemoteCacheManager createRemoteCacheManager() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
                .addServer()
                .host(infinispanJsonObject.getString("host"))
                .port(infinispanJsonObject.getInteger("port"))
                .security()
                .authentication()
                .username(infinispanJsonObject.getString("username"))
                .password(infinispanJsonObject.getString("password"))
                .connectionPool()
                .exhaustedAction(ExhaustedAction.CREATE_NEW);

        RemoteCacheManager remoteCacheManager = new RemoteCacheManager(builder.build());

        // Ensure the cache exists
        try {
            remoteCacheManager
                    .administration()
                    .getOrCreateCache("vertx-web.sessions",
                            new org.infinispan.configuration.cache.ConfigurationBuilder()
                                    .clustering()
                                    .cacheMode(CacheMode.DIST_SYNC)
                                    .statistics()
                                    .expiration()
                                    .lifespan(24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS)
                                    .maxIdle(24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS)
                                    .encoding()
                                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return remoteCacheManager;
    }

}
