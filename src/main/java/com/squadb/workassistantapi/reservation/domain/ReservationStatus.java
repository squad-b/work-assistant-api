package com.squadb.workassistantapi.reservation.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum ReservationStatus {
    WAITING,
    FINISHED, //대여완료
    CANCELED, //요청에 의한 취소
    REVOKED; //만료일이 지나서 수행된 취소

    boolean isWaiting() {
        return this == WAITING;
    }

    boolean isFinished() {
        return this == FINISHED;
    }
}
