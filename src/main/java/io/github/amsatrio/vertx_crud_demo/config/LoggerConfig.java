package io.github.amsatrio.vertx_crud_demo.config;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class LoggerConfig {
    public void init(JsonObject jsonObject) {
        int loggerLevelInt = jsonObject.getJsonObject("monitoring").getJsonObject("logger").getInteger("level");
        String loggerLevel = "INFO";

        switch (loggerLevelInt) {
            case 1:
                loggerLevel = "ERROR";
                break;
            case 2:
                loggerLevel = "WARN";
                break;
            case 3:
                loggerLevel = "INFO";
                break;
            case 4:
                loggerLevel = "DEBUG";
                break;
            case 5:
                loggerLevel = "TRACE";
                break;
        }

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.setLevel(Level.toLevel(loggerLevel));
    }
}
