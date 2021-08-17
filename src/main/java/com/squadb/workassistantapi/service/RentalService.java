package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.domain.exceptions.NotRentableException;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Slf4j
@RequiredArgsConstructor
@Service
public class RentalService {
    private final MemberService memberService;
    private final BookService bookService;
    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Rental findById(final Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> new IllegalArgumentException(String.format("No rental:[%d]", rentalId)));
    }

    @Transactional
    public Long rentBook(final Long bookId, final Long memberId, final boolean isLongTerm) {
        final Book book = bookService.findById(bookId);
        final Member member = memberService.findById(memberId);
        final Rental rental = Rental.createRental(book, member, isLongTerm, now());
        final Rental saveRental = rentalRepository.save(rental);
        finishReservation(member, book);
        return saveRental.getId();
    }

    private void finishReservation(Member member, Book book) {
        Optional<Reservation> optionalReservation =
                reservationRepository.findWaitingReservationWithMemberByBookId(book.getId());
        try {
            optionalReservation.ifPresent(reservation -> reservation.finishedBy(member));
        } catch (ReservationException e) {
            throw new NotRentableException("예약회원이 있는 책은 대여할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllByBook(final Long bookId) {
        final Book book = bookService.findById(bookId);
        return rentalRepository.findAllByBook(book);
    }

    @Transactional(readOnly = true)
    public List<Rental> findMemberBookRentals(final Long memberId, final RentalStatus rentalStatus) {
        final Member member = memberService.findById(memberId);
        return rentalStatus == null ? rentalRepository.findAllByMember(member) : rentalRepository.findAllByMemberAndStatus(member, rentalStatus);
    }

    @Transactional
    public void returnBooks(List<Long> rentalIdList, LoginMember loginMember) {
        final List<Rental> rentalList = rentalRepository.findAllById(rentalIdList);
        if (rentalList.isEmpty()) {
            log.warn("존재하지 않는 대여 목록 입니다. {}", rentalIdList);
            return;
        }
        rentalList.forEach(rental -> rental.returnBy(loginMember));
    }
}
