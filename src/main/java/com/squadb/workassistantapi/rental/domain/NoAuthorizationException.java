package com.squadb.workassistantapi.rental.domain;

public class NoAuthorizationException extends RuntimeException {
    public NoAuthorizationException(String message) {
        super(message);
    }
}
