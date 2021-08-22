package com.squadb.workassistantapi.book.domain;

public class NotRentableException extends RuntimeException {
    public NotRentableException(String message) {
        super(message);
    }
}
