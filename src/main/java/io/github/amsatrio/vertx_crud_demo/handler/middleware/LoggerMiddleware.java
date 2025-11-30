package io.github.amsatrio.vertx_crud_demo.handler.middleware;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Singleton
public class LoggerMiddleware implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext event) {
        log.debug("incoming");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date startDate = new Date();

        event.addEndHandler(result -> {
            log.debug("outgoing");

            Date endDate = new Date();
            long elapsedTime = endDate.getTime() - startDate.getTime();

            JsonObject jsonObject = new JsonObject();
            jsonObject.put("path", event.normalizedPath());
            jsonObject.put("remote_address", event.request().remoteAddress().host());
            jsonObject.put("method", event.request().method().name());
            jsonObject.put("accessed_on", simpleDateFormat.format(startDate));
            jsonObject.put("status_code", event.response().getStatusCode());
            jsonObject.put("elapsed_time", elapsedTime);
            log.info(jsonObject.encode());
        });

        event.next();

    }
}
