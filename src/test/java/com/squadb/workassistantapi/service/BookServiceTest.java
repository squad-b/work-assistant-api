package com.squadb.workassistantapi.service;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;
import com.squadb.workassistantapi.service.exception.KeyDuplicationException;

@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired private BookService bookService;
    @Autowired private EntityManager entityManager;

    @DisplayName("책을 등록하면 책의 registerDate 가 세팅된다.")
    @Test
    public void bookRegisterTest() {
        //given
        final Member member = createMember();
        final Book book = createBook("1234567890123");

        //when
        long registerBookId = bookService.register(book, member.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        Book findBook = bookService.findById(registerBookId);
        assertThat(book.getId()).isEqualTo(findBook.getId());
        assertThat(book.getRegistrationDate()).isNotNull();
    }

    @DisplayName("책 등록시 isbn 이 중복되면 예외가 발생한다.")
    @Test
    public void isbnDuplicationTest() {
        //given
        final String isbn = "1234567890123";
        final Member member = createMember();
        final Book book = createBook(isbn);
        bookService.register(book, member.getId());

        //when then
        final Book other = createBook(isbn);
        assertThatThrownBy(() -> bookService.register(other, member.getId())).isInstanceOf(KeyDuplicationException.class);
    }

    private Member createMember() {
        final Member member = Member.createMember("admin@test.com", "피플팀", "1234", MemberType.ADMIN);
        entityManager.persist(member);
        entityManager.flush();
        return member;
    }

    private Book createBook(String isbn) {
        return Book.builder().isbn(isbn)
                .title("title")
                .stockQuantity(1)
                .build();
    }

}