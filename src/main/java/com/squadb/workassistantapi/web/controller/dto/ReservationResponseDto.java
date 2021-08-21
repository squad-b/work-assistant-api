package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.BookCategory;
import com.squadb.workassistantapi.domain.Reservation;
import com.squadb.workassistantapi.domain.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class ReservationResponseDto {

    private String memberName;
    private BookCategory bookCategory;
    private ReservationStatus reservationStatus;
    private LocalDateTime reservationDate;

    public static ReservationResponseDto of(Reservation reservation) {
        String memberName = reservation.getMember().getName();
        BookCategory bookCategory = reservation.getBook().getCategory();
        ReservationStatus reservationStatus = reservation.getStatus();
        LocalDateTime reservationDate = reservation.getReservationDate();
        return new ReservationResponseDto(memberName, bookCategory, reservationStatus, reservationDate);
    }
}
