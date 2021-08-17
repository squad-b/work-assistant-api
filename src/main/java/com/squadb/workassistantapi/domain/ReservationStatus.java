package com.squadb.workassistantapi.domain;

public enum ReservationStatus {
    WAITING,
    FINISHED, //대여완료
    CANCELED, //요청에 의한 취소
    REVOKED //만료일이 지나서 수행된 취소
}
