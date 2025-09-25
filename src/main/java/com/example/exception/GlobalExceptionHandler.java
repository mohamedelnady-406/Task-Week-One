package com.example.exception;

import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Produces
@Singleton
public class GlobalExceptionHandler implements ExceptionHandler<Throwable, HttpResponse<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public HttpResponse<?> handle(HttpRequest request, Throwable exception) {
        LOG.error("Unhandled exception on {}: {}", request.getPath(), exception.getMessage(), exception);
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", exception.getMessage(),
                        "path", request.getPath()
                ));
    }
}
