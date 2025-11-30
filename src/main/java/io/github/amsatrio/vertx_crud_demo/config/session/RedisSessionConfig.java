package io.github.amsatrio.vertx_crud_demo.config.session;

import io.github.amsatrio.vertx_crud_demo.VertxSingletonHolder;
import io.github.amsatrio.vertx_crud_demo.config.ApplicationConfig;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.redis.RedisSessionStore;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class RedisSessionConfig implements SessionProvider {
    private final JsonObject sessionJsonObject;
    private final JsonObject redisJsonObject;

    @Inject
    public RedisSessionConfig(ApplicationConfig applicationConfig) {
        this.sessionJsonObject = applicationConfig.getJsonObject().getJsonObject("security").getJsonObject("session");
        this.redisJsonObject = applicationConfig.getJsonObject().getJsonObject("connection").getJsonObject("redis");
    }

    @Override
    public SessionHandler getSessionHandler() {
        RedisOptions options = getRedisOptions();
        Redis redis = Redis.createClient(VertxSingletonHolder.vertx(), options);
        RedisSessionStore sessionStore = RedisSessionStore.create(VertxSingletonHolder.vertx(), redis);
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

    private RedisOptions getRedisOptions() {
        RedisOptions options = new RedisOptions();
        String redisUrl = "redis://:" + redisJsonObject.getString("password") + "@"
                + redisJsonObject.getString("host")
                + ":" + redisJsonObject.getInteger("port") + "/";

        options.setConnectionString(redisUrl);
        NetClientOptions tcpOptions = options.getNetClientOptions();
        tcpOptions
                .setSsl(redisJsonObject.getJsonObject("ssl").getBoolean("enabled"))
                .setTrustOptions(
                        new PemTrustOptions()
                                .addCertPath(redisJsonObject.getJsonObject("ssl").getString("cert_path")))
                // Algo can be the empty string "" or "HTTPS" to verify the server hostname
                .setHostnameVerificationAlgorithm("HTTPS");
        return options;
    }
}
