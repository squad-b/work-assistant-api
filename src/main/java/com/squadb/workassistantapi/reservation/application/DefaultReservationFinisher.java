package com.squadb.workassistantapi.reservation.application;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationFinisher;
import com.squadb.workassistantapi.reservation.domain.ReservationRepository;
import com.squadb.workassistantapi.reservation.domain.ReservationStatus;
import com.squadb.workassistantapi.reservation.dto.ReservationSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultReservationFinisher implements ReservationFinisher {

    private final ReservationRepository reservationRepository;

    @Override
    public void finish(Book book, Member member) {
        Optional<Reservation> optionalWaitingReservation = findWaitingReservationByMemberIdAndBookId(member, book);
        optionalWaitingReservation.ifPresent(reservation -> reservation.finishedBy(member));
    }

    private Optional<Reservation> findWaitingReservationByMemberIdAndBookId(Member member, Book book) {
        ReservationSearchDto reservationSearchDto = new ReservationSearchDto(member.getId(), book.getId(), ReservationStatus.WAITING);
        return reservationRepository.findReservationWithMemberBySearch(reservationSearchDto);
    }
}
