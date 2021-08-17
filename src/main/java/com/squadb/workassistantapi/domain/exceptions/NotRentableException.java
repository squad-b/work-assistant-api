package com.squadb.workassistantapi.domain.exceptions;

public class NotRentableException extends RuntimeException {
    public NotRentableException(String message) {
        super(message);
    }
}
