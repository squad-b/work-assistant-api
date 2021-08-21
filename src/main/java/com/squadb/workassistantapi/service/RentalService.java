package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.domain.exceptions.NotRentableException;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.controller.dto.ReservationSearchDto;
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
    private final ReservationValidator reservationValidator;

    @Transactional(readOnly = true)
    public Rental findById(final Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> new IllegalArgumentException(String.format("No rental:[%d]", rentalId)));
    }

    @Transactional
    public Long rentBook(final Long bookId, final Long memberId, final boolean isLongTerm) {
        final Book book = bookService.findById(bookId);
        final Member member = memberService.findById(memberId);
        validateNotExistsOtherMemberReservation(book, member);

        final Rental rental = Rental.createRental(book, member, isLongTerm, now());
        final Rental saveRental = rentalRepository.save(rental);
        finishReservation(member, book);
        return saveRental.getId();
    }

    private void validateNotExistsOtherMemberReservation(Book book, Member member) {
        try {
            reservationValidator.notExistsOtherMemberReservation(book, member);
        } catch (IllegalArgumentException e) {
            throw new NotRentableException("권한이 없습니다.");
        }
    }

    private void finishReservation(Member member, Book book) {
        Optional<Reservation> optionalReservation = findWaitingReservationByMemberIdAndBookId(member, book);
        try {
            optionalReservation.ifPresent(reservation -> reservation.finishedBy(member));
        } catch (IllegalArgumentException e) {
            throw new NotRentableException("권한이 없습니다.");
        }
    }

    private Optional<Reservation> findWaitingReservationByMemberIdAndBookId(Member member, Book book) {
        Long memberId = member.getId();
        Long bookId = book.getId();
        ReservationStatus status = ReservationStatus.WAITING;
        ReservationSearchDto reservationSearchDto = new ReservationSearchDto(memberId, bookId, status);
        return reservationRepository.findReservationWithMemberBySearch(reservationSearchDto);
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
