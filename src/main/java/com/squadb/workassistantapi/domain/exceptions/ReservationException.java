package com.squadb.workassistantapi.domain.exceptions;

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
