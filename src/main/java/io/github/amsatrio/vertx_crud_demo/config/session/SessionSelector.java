package io.github.amsatrio.vertx_crud_demo.config.session;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.ext.web.handler.SessionHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class SessionSelector {
    private final Map<SessionType, SessionProvider> map;

    @Inject
    public SessionSelector(Set<SessionProvider> providers) {
        this.map = providers.stream()
                .collect(Collectors.toMap(
                        provider -> {
                            if (provider instanceof DefaultSessionConfig) {
                                return SessionType.DEFAULT;
                            } else if (provider instanceof LocalSessionConfig) {
                                return SessionType.LOCAL;
                            } else if (provider instanceof InfinispanSessionConfig) {
                                return SessionType.INFINISPAN;
                            } else if (provider instanceof RedisSessionConfig) {
                                return SessionType.REDIS;
                            }
                            throw new IllegalArgumentException("Unknown session provider: " + provider.getClass());
                        },
                        Function.identity()));
    }

    public SessionHandler getSessionHandler(SessionType sessionType) {
        SessionProvider provider = map.get(sessionType);
        if (provider == null) {
            throw new IllegalArgumentException("No provider found for session type: " + sessionType);
        }
        return provider.getSessionHandler();
    }
}