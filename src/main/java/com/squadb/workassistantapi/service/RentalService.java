package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Rental;
import com.squadb.workassistantapi.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public long rentBook(long bookId, long memberId, boolean isLongTerm) {
        final Book book = bookService.findById(bookId);
        final Member member = memberService.findById(memberId);
        final Rental rental = Rental.createRental(book, member, isLongTerm);
        final Rental saveRental = rentalRepository.save(rental);
        return saveRental.getId();
    }
}
