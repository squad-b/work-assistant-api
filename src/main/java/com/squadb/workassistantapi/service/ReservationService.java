package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Reservation;
import com.squadb.workassistantapi.domain.ReservationRepository;
import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import com.squadb.workassistantapi.web.controller.dto.ReservationSearchDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationService {

    private final MemberService memberService;
    private final BookService bookService;

    private final ReservationRepository reservationRepository;

    public List<Reservation> findAllWaitingReservationByMemberId(Long memberId) {
        return reservationRepository.findAllWaitingReservationByMemberId(memberId);
    }

    public Long reserve(Long bookId, Long memberId) {
        Member member = memberService.findById(memberId);
        Book book = bookService.findById(bookId);
        validateCanReserve(member, book);

        Reservation reservation = Reservation.createReservation(member, book);
        reservationRepository.save(reservation);
        return reservation.getId();
    }

    private void validateCanReserve(Member member, Book book) {
        Optional<Reservation> optionalReservation = reservationRepository.findWaitingReservationWithMemberByBookId(book.getId());
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

    public Long cancel(Long reservationId, Long memberId) {
        Member member = memberService.findById(memberId);
        Reservation reservation = findReservationWithMemberById(reservationId);
        reservation.cancelBy(member);
        return reservation.getId();
    }

    private Reservation findReservationWithMemberById(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findReservationWithMemberById(reservationId);
        if (optionalReservation.isEmpty()) {
            String errorMessage = String.format("No Reservation:[%d]", reservationId);
            throw new ReservationException(ReservationErrorCode.NOT_FOUND, errorMessage);
        }
        return optionalReservation.get();
    }

    public Page<Reservation> findAllReservation(ReservationSearchDto reservationSearchDto, Pageable pageable) {
        return reservationRepository.findAllReservation(reservationSearchDto, pageable);
    }

    /**
     * @return 취소된 예약 개수
     */
    public long revokeExpiredReservation() {
        return reservationRepository.findRentableReservation()
                .stream()
                .filter(reservation -> reservation.revokeReservationExpiringOn(LocalDateTime.now()))
                .count();
    }
}
