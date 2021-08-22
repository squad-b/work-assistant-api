package com.squadb.workassistantapi.reservation.dto;

import com.squadb.workassistantapi.book.domain.BookCategory;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
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
