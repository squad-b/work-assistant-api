package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationSearchDto {

    private Long memberId;
    private Long bookId;
    private ReservationStatus reservationStatus;
}
