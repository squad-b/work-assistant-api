package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationService {

    private final MemberService memberService;

    private final BookService bookService;

    private final ReservationRepository reservationRepository;

    public Long reserve(Long memberId, Long bookId) {
        Member member = memberService.findById(memberId);
        Book book = bookService.findById(bookId);
        validateCanReserve(member, book);

        Reservation reservation = Reservation.createReservation(member, book);
        reservationRepository.save(reservation);
        return reservation.getId();
    }

    private void validateCanReserve(Member member, Book book) {
        Optional<Reservation> optionalReservation = findWaitingReservationWithMemberByBookId(book.getId());
        //검증성공 로직
        if (optionalReservation.isEmpty()) {
            return;
        }

        //검증실패 로직
        Reservation reservation = optionalReservation.get();
        if (reservation.isReservedBy(member)) {
            throw new ReservationException(ReservationErrorCode.ALREADY_MYSELF_RESERVED);
        }
        throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
    }

    private Optional<Reservation> findWaitingReservationWithMemberByBookId(Long bookId) {
        return reservationRepository.findReservationWithMemberByBookIdAndStatus(bookId, ReservationStatus.WAITING);
    }

    public void cancel(Long reservationId, Long memberId) {
        Member member = memberService.findById(memberId);
        Reservation reservation = findReservationWithMemberById(reservationId);
        reservation.cancelBy(member);
    }

    private Reservation findReservationWithMemberById(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findReservationWithMemberById(reservationId);
        if (optionalReservation.isEmpty()) {
            throw new ReservationException(ReservationErrorCode.NOT_FOUND);
        }
        return optionalReservation.get();
    }
}
