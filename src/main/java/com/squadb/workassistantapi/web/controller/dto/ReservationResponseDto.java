package com.squadb.workassistantapi.web.controller.dto;

import lombok.Getter;

@Getter
public class ReservationResponseDto {

    private Long reservationId;
    private String message;

    public static ReservationResponseDto ok(Long reservationId) {
        ReservationResponseDto reservationResponseDto = new ReservationResponseDto();
        reservationResponseDto.reservationId = reservationId;
        return reservationResponseDto;
    }

    public static ReservationResponseDto fail(String message) {
        ReservationResponseDto reservationResponseDto = new ReservationResponseDto();
        reservationResponseDto.message = message;
        return reservationResponseDto;
    }
}
