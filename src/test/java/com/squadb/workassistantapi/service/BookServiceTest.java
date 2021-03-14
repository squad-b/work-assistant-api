package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired private BookService bookService;
    @Autowired private EntityManager entityManager;

    @DisplayName("책을 등록하면 책의 registerDate 가 세팅된다.")
    @Test
    public void bookRegisterTest() {
        //given
        final Member member = Member.createMember("admin@test.com", "1234", MemberType.ADMIN);
        entityManager.persist(member);
        entityManager.flush();

        final Book book = Book.builder().isbn("1234567890123")
                .title("title")
                .stockQuantity(1)
                .build();

        //when
        long registerBookId = bookService.register(book, member.getId());
        entityManager.flush();
        entityManager.clear();

        //then
        Book findBook = bookService.findById(registerBookId);
        assertThat(book.getId()).isEqualTo(findBook.getId());
        assertThat(book.getRegistrationDate()).isNotNull();
    }

}