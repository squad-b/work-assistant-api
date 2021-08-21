package com.squadb.workassistantapi.reservation.application;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.BookRepository;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberRepository;
import com.squadb.workassistantapi.reservation.domain.*;
import com.squadb.workassistantapi.reservation.dto.ReservationSearchAllDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationService {

    private final ReservationValidator reservationValidator;

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    public Page<Reservation> findMyReservation(Long memberId, Pageable pageable) {
        return findAllWaitingReservationByMemberId(memberId, pageable);
    }

    private Page<Reservation> findAllWaitingReservationByMemberId(Long memberId, Pageable pageable) {
        return reservationRepository.findAllByMemberIdAndStatus(memberId, ReservationStatus.WAITING,pageable);
    }

    public Long reserve(Long bookId, Long memberId) {
        Book book = findBookById(bookId);
        Member member = findMemberById(memberId);
        Reservation reservation = Reservation.createReservation(member, book, reservationValidator);
        return reservationRepository.save(reservation).getId();
    }

    private Book findBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.REQUIRED_RESERVATION));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.REQUIRED_RESERVATION));
    }

    public Long cancel(Long reservationId, Long memberId) {
        Member member = findMemberById(memberId);
        Reservation reservation = findReservationById(reservationId);
        reservation.cancelBy(member);
        return reservation.getId();
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findReservationWithMemberById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.NOT_FOUND));
    }

    public Page<Reservation> findAllReservation(ReservationSearchAllDto reservationSearchAllDto, Pageable pageable) {
        return reservationRepository.findAllBySearchAll(reservationSearchAllDto, pageable);
    }

    /**
     * 대여할 수 있지만 만료기간이 지날 때 까지 대여를 하지 않은 예약들을 취소시킨다.
     * @return 예약 만료기간이 지나서 취소된 예약 개수
     */
    public long revokeExpiredReservation() {
        return findWaitingReservationByBookId().stream()
                .filter(Reservation::canRentable)
                .filter(reservation -> reservation.revokeReservationExpiringOn(LocalDateTime.now()))
                .count();
    }

    private List<Reservation> findWaitingReservationByBookId() {
        return reservationRepository.findAllWithBookByStatus(ReservationStatus.WAITING);
    }
}
