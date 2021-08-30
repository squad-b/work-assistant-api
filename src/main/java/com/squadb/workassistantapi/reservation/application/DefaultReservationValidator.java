package com.squadb.workassistantapi.reservation.application;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.reservation.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class DefaultReservationValidator implements ReservationValidator {

    private final ReservationRepository reservationRepository;

    @Override
    public void validateCanReserve(Member member, Book book) {
        List<Reservation> waitingReservations = reservationRepository.findAllByBookIdAndStatus(book.getId(), ReservationStatus.WAITING);
        validateNotExistsMyReservation(member, waitingReservations);
        validateNotOverThanMaxReservationCountPerBook(waitingReservations);
        validateNotOverThanMaxReservationCountPerMember(member.getId());
        validateBookOutOfStock(book);
    }

    private void validateNotExistsMyReservation(Member member, List<Reservation> reservations) {
        boolean existsMyReservation = reservations.stream()
                .anyMatch(reservation -> reservation.isReservedBy(member));
        if (existsMyReservation) {
            throw new ReservationException(ReservationErrorCode.ALREADY_MYSELF_RESERVED);
        }
    }

    private void validateNotOverThanMaxReservationCountPerBook(List<Reservation> reservations) {
        if (reservations.size() >= Reservation.MAX_COUNT_PER_BOOK) {
            String errorMessage = String.format("한 종의 책당 최대 예약 가능 개수는 %d개 입니다.", Reservation.MAX_COUNT_PER_BOOK);
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, errorMessage);
        }
    }

    private void validateNotOverThanMaxReservationCountPerMember(Long memberId) {
        List<Reservation> waitingReservations = reservationRepository.findAllByMemberIdAndStatus(memberId, ReservationStatus.WAITING);
        if (waitingReservations.size() >= Reservation.MAX_COUNT_PER_MEMBER) {
            String errorMessage = String.format("1인당 최대 예약 가능 개수는 %d개 입니다.", Reservation.MAX_COUNT_PER_MEMBER);
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, errorMessage);
        }
    }

    private void validateBookOutOfStock(Book book) {
        if (!book.isOutOfStock()) {
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, "대여가능한 책은 예약할 수 없습니다.");
        }
    }
}
