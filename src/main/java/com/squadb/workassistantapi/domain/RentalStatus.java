package com.squadb.workassistantapi.domain;

public enum RentalStatus {
    ON_RENTAL,
    RETURN;

    boolean onRental() {
        return this.equals(ON_RENTAL);
    }
}
