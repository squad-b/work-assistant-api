package com.squadb.workassistantapi.reservation.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReservationErrorCode {

    NOT_RESERVABLE("예약할 수 없습니다."),
    NOT_FOUND("존재하지 않는 예약입니다."),
    NOT_AUTHORIZED("권한이 없습니다."),
    NOT_SUPPORTED("지원하지 않는 형식입니다"),
    REQUIRED_RESERVATION("예약 필수 정보가 없습니다."),
    ALREADY_RESERVED("이미 예약자가 있는 책입니다."),
    ALREADY_MYSELF_RESERVED("이미 예약한 책입니다."),
    ILLEGAL_STATUS("옳바르지 않은 예약 상태입니다.");

    private final String defaultErrorMessage;
}
