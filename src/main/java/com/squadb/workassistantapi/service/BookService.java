package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.BookRepository;
import com.squadb.workassistantapi.service.exception.KeyDuplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
        checkIsbnDuplication(book.getIsbn());
        final Member member = memberService.findById(registrantId);
        book.setRegistrant(member);
        final Book saveBook = bookRepository.save(book);
        return saveBook.getId();
    }

    private void checkIsbnDuplication(final String isbn) {
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new KeyDuplicationException("key duplication book : [" + isbn + "]");
        }
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(Sort sort) {
        return bookRepository.findAll(sort);
    }
}
