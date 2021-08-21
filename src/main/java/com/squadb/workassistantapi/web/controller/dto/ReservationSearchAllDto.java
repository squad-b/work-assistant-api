package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.ReservationStatus;
import lombok.Getter;

@Getter
public class ReservationSearchAllDto {

    private Long reservationId;
    private String memberName;
    private String bookTitle;
    private ReservationStatus reservationStatus;
}
