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
}
