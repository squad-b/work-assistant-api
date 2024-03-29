package com.squadb.workassistantapi.book.domain;

import com.squadb.workassistantapi.rental.domain.NoAuthorizationException;
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
                .build())
                .isInstanceOf(NoAuthorizationException.class)
                .hasMessage("관리자만 책을 등록, 수정할 수 있습니다.");
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

    @DisplayName("책의 수정은 관리자만 가능하다")
    @Test
    void updateBookNormalMemberTest() {
        // when, then
        assertThatThrownBy(() -> book.update(DEVELOP, StockQuantity.valueOf(2), 고객A))
                .isInstanceOf(NoAuthorizationException.class)
                .hasMessage("관리자만 책을 등록, 수정할 수 있습니다.");
    }

    @DisplayName("책 수정시 수량이 null일 수 없다.")
    @Test
    void updateBookWithNullStockQuantityTest() {
        // when, then
        assertThatThrownBy(() -> book.update(DEVELOP, null, 관리자))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("책 수량이 null일 수 없습니다.");
    }

    @DisplayName("책 수정 기능이 정상적으로 동작한다.")
    @Test
    void updateBookTest() {
        // when
        book.update(DEVELOP, StockQuantity.valueOf(2), 관리자);

        // then
        assertEquals(book.getCategory(), DEVELOP);
        assertEquals(book.getStockQuantityValue(), 2);
    }
}
