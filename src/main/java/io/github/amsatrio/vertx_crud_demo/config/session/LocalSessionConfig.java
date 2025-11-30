package io.github.amsatrio.vertx_crud_demo.config.session;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LocalSessionConfig implements SessionProvider {
    private final JsonObject sessionJsonObject;

    @Inject
    public LocalSessionConfig(ApplicationConfig applicationConfig) {
        this.sessionJsonObject = applicationConfig.getJsonObject().getJsonObject("security")
                .getJsonObject("session");
    }

    @Override
    public SessionHandler getSessionHandler() {
        LocalSessionStore sessionStore = LocalSessionStore.create(VertxSingletonHolder.vertx());
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

}
