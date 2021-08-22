package com.squadb.workassistantapi.reservation.dto;

import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
import lombok.Getter;

@Getter
public class ReservationSearchAllDto {

    private Long reservationId;
    private String memberName;
    private String bookTitle;
    private ReservationStatus reservationStatus;
}
