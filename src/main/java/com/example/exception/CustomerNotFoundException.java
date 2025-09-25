package com.example.exception;

import io.micronaut.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomerNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public CustomerNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

}
