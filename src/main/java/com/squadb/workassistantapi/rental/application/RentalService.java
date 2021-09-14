package com.squadb.workassistantapi.rental.application;

import com.squadb.workassistantapi.book.application.BookService;
import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.application.MemberService;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.rental.domain.Rental;
import com.squadb.workassistantapi.rental.domain.RentalRepository;
import com.squadb.workassistantapi.rental.domain.RentalStatus;
import com.squadb.workassistantapi.rental.domain.RentalValidator;
import com.squadb.workassistantapi.reservation.domain.ReservationFinisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Slf4j
@RequiredArgsConstructor
@Service
public class RentalService {
    private final MemberService memberService;
    private final BookService bookService;
    private final RentalRepository rentalRepository;
    private final RentalValidator rentalValidator;
    private final ReservationFinisher reservationFinisher;

    @Transactional(readOnly = true)
    public Rental findById(final Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> new IllegalArgumentException(String.format("No rental:[%d]", rentalId)));
    }

    @Transactional
    public Long rentBook(final Long bookId, final Long memberId, final boolean isLongTerm) {
        final Book book = bookService.findById(bookId);
        final Member member = memberService.findById(memberId);
        final Rental rental = Rental.createRental(book, member, isLongTerm, now(), rentalValidator, reservationFinisher);
        final Rental saveRental = rentalRepository.save(rental);
        return saveRental.getId();
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
        final Member member = memberService.findById(loginMember.getId());
        final LocalDateTime returnDate = now();
        rentalList.forEach(rental -> rental.returnBy(member, returnDate));
    }
}
