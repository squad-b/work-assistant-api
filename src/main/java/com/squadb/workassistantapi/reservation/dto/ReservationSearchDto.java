package com.squadb.workassistantapi.reservation.dto;

import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationSearchDto {

    private Long memberId;
    private Long bookId;
    private ReservationStatus reservationStatus;

    // TODO: [2021/08/28 양동혁] enum binding exception 처리 
}
