package io.github.amsatrio.vertx_crud_demo.config.session;

import io.vertx.ext.web.handler.SessionHandler;

public interface SessionProvider {
    SessionHandler getSessionHandler();
}
