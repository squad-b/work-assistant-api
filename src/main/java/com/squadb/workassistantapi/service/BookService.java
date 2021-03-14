package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Book findById(final long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Book:[%d]", bookId)));
    }

    @Transactional
    public long register(final Book book, final long registrantId) {
        final Member member = memberService.findById(registrantId);
        book.setRegistrant(member);
        final Book saveBook = bookRepository.save(book);
        return saveBook.getId();
    }
}
