package com.squadb.workassistantapi.domain.exceptions;

public class NoAuthorizationException extends RuntimeException {
    public NoAuthorizationException(String message) {
        super(message);
    }
}
