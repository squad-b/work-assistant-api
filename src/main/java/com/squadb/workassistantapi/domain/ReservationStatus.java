package com.squadb.workassistantapi.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor
public enum ReservationStatus {
    WAITING,
    FINISHED, //대여완료
    CANCELED, //요청에 의한 취소
    REVOKED; //만료일이 지나서 수행된 취소

    @JsonCreator
    public static ReservationStatus from(String requestStatus) {
        return Arrays.stream(ReservationStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(requestStatus))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
