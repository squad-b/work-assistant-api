package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Rental;
import com.squadb.workassistantapi.domain.RentalStatus;
import com.squadb.workassistantapi.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RentalService {
    private final MemberService memberService;
    private final BookService bookService;
    private final RentalRepository rentalRepository;

    @Transactional(readOnly = true)
    public Rental findById(final long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> new IllegalArgumentException(String.format("No rental:[%d]", rentalId)));
    }

    @Transactional
    public long rentBook(final long bookId, final long memberId, final boolean isLongTerm) {
        final Book book = bookService.findById(bookId);
        final Member member = memberService.findById(memberId);
        final Rental rental = Rental.createRental(book, member, isLongTerm);
        final Rental saveRental = rentalRepository.save(rental);
        return saveRental.getId();
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllByBook(final long bookId) {
        final Book book = bookService.findById(bookId);
        return rentalRepository.findAllByBook(book);
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllByMember(final long memberId) {
        final Member member = memberService.findById(memberId);
        return rentalRepository.findAllByMember(member);
    }

    @Transactional
    public long updateRental(long rentalId, long memberId, RentalStatus status) {
        final Rental rental = rentalRepository.findById(rentalId).orElseThrow();
        rental.updateRental(memberId, status);
        return rental.getId();
    }
}
