package com.squadb.workassistantapi.reservation.dto;

import com.squadb.workassistantapi.book.domain.BookCategory;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReservationResponseDto {

    private String memberName;
    private BookCategory bookCategory;
    private ReservationStatus reservationStatus;
    private LocalDateTime reservationDate;

    private ReservationResponseDto(Reservation reservation) {
        memberName = reservation.getMemberName();
        bookCategory = reservation.getBookCategory();
        reservationStatus = reservation.getStatus();
        reservationDate = reservation.getReservationDate();
    }

    public static List<ReservationResponseDto> list(Page<Reservation> pageResult) {
        return pageResult.getContent().stream()
                .map(ReservationResponseDto::new)
                .collect(Collectors.toList());
    }
}
