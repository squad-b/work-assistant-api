package com.squadb.workassistantapi.book.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.squadb.workassistantapi.book.domain.BookCategory.DEVELOP;
import static com.squadb.workassistantapi.book.domain.IsbnTest.isbn;
import static com.squadb.workassistantapi.member.domain.MemberTest.고객A;
import static com.squadb.workassistantapi.member.domain.MemberTest.관리자;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .isbn(isbn)
                .title("Spring")
                .stockQuantity(StockQuantity.valueOf(1))
                .registrationDate(LocalDateTime.now())
                .registrant(관리자)
                .build();
    }

    @DisplayName("책의 등록자는 관리자만 가능하다")
    @Test
    void createBookNormalMemberTest() {
        //when then
        assertThatThrownBy(() -> Book.builder()
                .isbn(isbn)
                .title("Spring")
                .stockQuantity(StockQuantity.valueOf(1))
                .registrationDate(LocalDateTime.now())
                .registrant(고객A)
                .build());
    }

    @DisplayName("책의 수정은 관리자만 가능하다")
    @Test
    void updateBookNormalMemberTest() {
        // when, then
        assertThatThrownBy(() -> book.update(DEVELOP, 2, 고객A));
    }

    @DisplayName("책 수정 기능이 정상적으로 동작한다.")
    @Test
    void updateBookTest() {
        // when
        book.update(DEVELOP, 2, 관리자);

        // then
        assertEquals(book.getCategory(), DEVELOP);
        assertEquals(book.getStockQuantityValue(), 2);
    }

    @DisplayName("책의 isbn, 제목, 재고, 등록일, 등록자는 필수정보이다.")
    @Test
    void createBookWithEssentialInformationTest() {
        // given
        final LocalDateTime registrantDateTime = LocalDateTime.now();

        // when
        final Book book = Book.builder()
                .isbn(isbn)
                .title("Spring")
                .stockQuantity(StockQuantity.valueOf(1))
                .registrant(관리자)
                .registrationDate(registrantDateTime)
                .build();

        // then
        assertThat(book.getIsbn()).isEqualTo(isbn);
        assertThat(book.getTitle()).isEqualTo("Spring");
        assertThat(book.getStockQuantity().getValue()).isEqualTo(1);
        assertThat(book.getRegistrationDate()).isEqualTo(registrantDateTime);
    }
}
