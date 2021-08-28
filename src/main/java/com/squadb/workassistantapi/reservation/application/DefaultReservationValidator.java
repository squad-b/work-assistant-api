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
        List<Reservation> waitingReservations = findAllWaitingReservationByBookId(book.getId());
        validateNotExistsMyReservation(member, waitingReservations);
        validateNotOverThanMaxReservationCountPerBook(waitingReservations);
        validateNotOverThanMaxReservationCountPerMember(member.getId());
    }

    private List<Reservation> findAllWaitingReservationByBookId(Long bookId) {
        return reservationRepository.findAllByBookIdAndStatus(bookId, ReservationStatus.WAITING);
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
        List<Reservation> reservationList = findAllWaitingReservationByMemberId(memberId);
        int maxCountPerMember = Reservation.MAX_COUNT_PER_MEMBER;
        if (reservationList.size() >= maxCountPerMember) {
            String errorMessage = String.format("1인당 최대 예약 가능 개수는 %d개 입니다.", maxCountPerMember);
            throw new ReservationException(ReservationErrorCode.NOT_RESERVABLE, errorMessage);
        }
    }

    private List<Reservation> findAllWaitingReservationByMemberId(Long memberId) {
        return reservationRepository.findAllByMemberIdAndStatus(memberId, ReservationStatus.WAITING);
    }

    public void notExistsOtherMemberReservation(Book book, Member member) {
        List<Reservation> reservationList = findAllWaitingReservationByBookId(book.getId());
        boolean existsOtherMemberReservation = reservationList.stream()
                .anyMatch(reservation -> !reservation.isReservedBy(member));
        if (existsOtherMemberReservation) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
        }
    }
}
