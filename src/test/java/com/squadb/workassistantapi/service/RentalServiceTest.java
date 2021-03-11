package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RentalServiceTest {
    @Autowired EntityManager entityManager;
    @Autowired RentalService rentalService;

    @DisplayName("기본 책 대여 테스트")
    @Test
    public void rentBookTest() {
        // givenR
        Member member = createMember();
        Book book = createBook();

        // when
        final long rentalId = rentalService.rentBook(book.getId(), member.getId(), false);
        entityManager.clear();
        entityManager.flush();

        // then
        Rental rental = rentalService.findById(rentalId);
        assertThat(rental.getMemberId()).isEqualTo(member.getId());
        assertThat(rental.getBookId()).isEqualTo(book.getId());
        assertThat(rentalId).isGreaterThan(0L);

        book = entityManager.find(Book.class, book.getId());
        System.out.println("=======" + book.stockQuantity);
    }

    private Book createBook() {
        Book book = Book.builder()
                .isbn("1234")
                .title("제목")
                .author("작가")
                .description("설명")
                .imageUrl("book.img.url")
                .category(BookCategory.DEVELOP)
                .publisher("출판사")
                .stockQuantity(1)
                .publishingDate(LocalDateTime.now())
                .build();
        entityManager.persist(book);
//        entityManager.clear();
//        entityManager.flush();
        return book;
    }

    private Member createMember() {
        Member member = Member.createMember("test@naver.com", "12345", MemberType.NORMAL);
        entityManager.persist(member);
//        entityManager.clear();
//        entityManager.flush();
        return member;
    }




}