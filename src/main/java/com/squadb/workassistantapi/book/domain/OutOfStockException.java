package com.squadb.workassistantapi.book.domain;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }
}
