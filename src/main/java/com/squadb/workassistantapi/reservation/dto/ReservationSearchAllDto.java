package com.squadb.workassistantapi.reservation.dto;

import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSearchAllDto {

    private Long reservationId;
    private String memberName;
    private String bookTitle;
    private ReservationStatus reservationStatus;
}
