package com.squadb.workassistantapi.rental.domain;

public enum RentalStatus {
    ON_RENTAL,
    RETURN;

    boolean onRental() {
        return this.equals(ON_RENTAL);
    }
}
