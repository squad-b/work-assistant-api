package com.squadb.workassistantapi.web.controller.dto;

import lombok.Getter;

@Getter
public class ReservationSearchDto {

    private ReservationSearchType searchType;
    private Long reservationId;
    private String memberName;
    private String bookTitle;
}
