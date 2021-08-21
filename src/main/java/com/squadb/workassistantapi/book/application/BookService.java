package com.squadb.workassistantapi.book.application;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.book.domain.BookRepository;
import com.squadb.workassistantapi.book.domain.Isbn;
import com.squadb.workassistantapi.book.dto.BookRegisterDto;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Book findById(final Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Book:[%d]", bookId)));
    }

    @Transactional
    public Long register(final BookRegisterDto bookDto, final Long registrantId) {
        checkIsbnDuplication(bookDto.getIsbn());
        final Member registrant = memberRepository.findById(registrantId).orElseThrow(() -> new IllegalArgumentException("책 등록자가 없습니다. " + registrantId));
        final Book saveBook = bookRepository.save(bookDto.toEntity(registrant));
        return saveBook.getId();
    }

    private void checkIsbnDuplication(final String isbn) {
        if (bookRepository.findByIsbn(Isbn.valueOf(isbn)).isPresent()) {
            throw new IsbnDuplicationException("key duplication book : [" + isbn + "]");
        }
    }

    @Transactional(readOnly = true)
    public List<Book> findAll(Sort sort) {
        return bookRepository.findAll(sort);
    }
}
