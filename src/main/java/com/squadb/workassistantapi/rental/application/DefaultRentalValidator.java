package com.squadb.workassistantapi.rental.application;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.rental.domain.RentalValidator;
import com.squadb.workassistantapi.reservation.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultRentalValidator implements RentalValidator {

    private final ReservationRepository reservationRepository;

    @Override
    public void validateNotExistsOtherMemberReservation(Book book, Member member) {
        List<Reservation> waitingReservations = reservationRepository.findAllByBookIdAndStatus(book.getId(), ReservationStatus.WAITING);
        boolean existsOtherMemberReservation = waitingReservations.stream()
                .anyMatch(reservation -> !reservation.isReservedBy(member));
        if (existsOtherMemberReservation) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
        }
    }
}
