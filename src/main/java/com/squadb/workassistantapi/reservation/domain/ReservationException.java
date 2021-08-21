package com.squadb.workassistantapi.reservation.domain;

public class ReservationException extends RuntimeException {

    private final ReservationErrorCode errorCode;

    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode.getDefaultErrorMessage());
        this.errorCode = errorCode;
    }

    public ReservationException(ReservationErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public ReservationErrorCode getErrorCode() {
        return errorCode;
    }
}
