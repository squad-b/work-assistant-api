package com.squadb.workassistantapi.domain.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReservationErrorCode {
    NOT_RESERVABLE("예약할 수 없는 책입니다."),
    REQUIRED_RESERVATION("예약 필수 정보가 없습니다."),
    ALREADY_RESERVED("이미 예약자가 있는 책입니다."),
    ALREADY_MYSELF_RESERVED("이미 예약한 책입니다.");

    private final String defaultErrorMessage;
}
