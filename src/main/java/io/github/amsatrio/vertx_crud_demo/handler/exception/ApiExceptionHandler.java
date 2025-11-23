package io.github.amsatrio.vertx_crud_demo.handler.exception;


import io.github.amsatrio.vertx_crud_demo.dto.exception.AuthenticationException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.AuthorizationException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.BadRequestException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.DataExistException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.DataNotFoundException;
import io.github.amsatrio.vertx_crud_demo.dto.response.Response;
import io.netty.resolver.dns.DnsErrorCauseException;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BodyProcessorException;
import io.vertx.ext.web.validation.ParameterProcessorException;
import io.vertx.ext.web.validation.RequestPredicateException;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ApiExceptionHandler {
   

    public void error(RoutingContext routingContext, Throwable exception) {
        Response<Object> response = Response.error(500, exception.getMessage(), exception.getStackTrace());

        log.error("error: ", exception);

        if (exception instanceof BadRequestException) {
            response.setStatus(400);
        } else if (exception instanceof DataExistException) {
            response.setStatus(400);
        } else if (exception instanceof DataNotFoundException) {
            response.setStatus(404);
        } else if (exception instanceof ParameterProcessorException) {
            response.setStatus(400);
        } else if (exception instanceof BodyProcessorException) {
            response.setStatus(400);
        } else if (exception instanceof RequestPredicateException) {
            response.setStatus(400);
        } else if (exception instanceof AuthenticationException) {
            response.setStatus(401);
        } else if (exception instanceof AuthorizationException) {
            response.setStatus(403);
        } else if (exception instanceof DnsErrorCauseException) {
            response.setStatus(500);
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.end(response.toBuffer());
    }

    public void tooLarge(RoutingContext routingContext) {
        Response<String> response = Response.error(413, "payload too large", routingContext.normalizedPath());
        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.end(response.toBuffer());
    }

    public void notFound(RoutingContext routingContext) {
        Response<String> response = Response.error(404, "data not found", routingContext.normalizedPath());
        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.end(response.toBuffer());
    }

    public void badRequest(RoutingContext routingContext) {
        Response<String> response = Response.error(404, "data not found", routingContext.normalizedPath());

        Throwable failure = routingContext.failure();
        if (failure != null) {
            response.setMessage(failure.getMessage());
            response.setTrace(failure.getStackTrace());
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.end(response.toBuffer());
    }

    public void internalServerError(RoutingContext routingContext) {
        Response<String> response = Response.error(500, "internal server error", routingContext.normalizedPath());

        Throwable failure = routingContext.failure();
        if (failure != null) {
            response.setMessage(failure.getMessage());
            response.setTrace(failure.getStackTrace());
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.end(response.toBuffer());
    }

    public void invalidMethod(RoutingContext routingContext) {
        Response<String> response = Response.error(405, "invalid method", routingContext.normalizedPath());

        Throwable failure = routingContext.failure();
        if (failure != null) {
            response.setMessage(failure.getMessage());
            response.setTrace(failure.getStackTrace());
        }

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(response.getStatus());
        routingContext.end(response.toBuffer());
    }

}
