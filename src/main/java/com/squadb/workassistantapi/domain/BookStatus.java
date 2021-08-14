package com.squadb.workassistantapi.domain;

public enum BookStatus {
    RESERVABLE,
    BORROWED;

    boolean canReserve() {
        return this.equals(RESERVABLE);
    }
}
