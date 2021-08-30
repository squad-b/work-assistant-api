package com.squadb.workassistantapi.rental.domain;

public class NotRentableException extends RuntimeException {
    public NotRentableException(String message) {
        super(message);
    }
}
