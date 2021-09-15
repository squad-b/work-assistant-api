package com.squadb.workassistantapi.rental.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.book.domain.StockQuantity;
import com.squadb.workassistantapi.reservation.domain.ReservationFinisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.squadb.workassistantapi.book.domain.IsbnTest.isbn;
import static com.squadb.workassistantapi.member.domain.MemberTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class RentalTest {

    private static final int INITIAL_BOOK_STOCK = 2;

    private Rental rental;
    private Book book;
    private LocalDateTime returnDate;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .registrant(관리자)
                .title("JPA")
                .isbn(isbn)
                .stockQuantity(StockQuantity.valueOf(INITIAL_BOOK_STOCK))
                .registrationDate(LocalDateTime.now())
                .build();
        rental = Rental.createRental(book, 일반회원1, false, LocalDateTime.now(), mock(RentalValidator.class), mock(ReservationFinisher.class));
        returnDate = LocalDateTime.now();
    }

    @DisplayName("책 대여시 책을 다른사람이 예약하고 있다면 책 대여를 할 수 없다.")
    @Test
    void test1() {

    }

    @DisplayName("유저가 예약한 책을 대여시 책 예약은 종료된다.")
    @Test
    void test2() {

    }

    @DisplayName("책을 대여시 기본 책 대여일은 14일이다.")
    @Test
    void test3() {

    }

    @DisplayName("책을 장기대여 하면 책 대여 마감일이 저장되지 않는다.")
    @Test
    void test4() {

    }

    @DisplayName("책 대여시 책의 재고가 하나 줄어야 한다.")
    @Test
    void createRentalTest() {
        // then
        assertThat(book.getStockQuantity()).isEqualTo(StockQuantity.valueOf(INITIAL_BOOK_STOCK - 1));
    }

    @DisplayName("일반회원은 자신이 빌린 책을 반납할 수 있다.")
    @Test
    void normalMemberBookReturnTest() {
        assertDoesNotThrow(() -> rental.returnBy(일반회원1, returnDate));
    }

    @DisplayName("관리자는 다른사람이 빌린 책도 반납할 수 있다.")
    @Test
    void adminMemberBookReturnTest() {
        assertDoesNotThrow(() -> rental.returnBy(관리자, returnDate));
    }

    @DisplayName("일반회원은 다른사람이 빌린 책을 반납할 수 없다.")
    @Test
    void normalMemberBookReturnFailTest() {
        // when then
        assertThatThrownBy(() -> rental.returnBy(일반회원2, returnDate))
                .isInstanceOf(NoAuthorizationException.class)
                .hasMessageContaining("관리자 또는 책의 대여자만 책 반납이 가능합니다.");
    }

    @DisplayName("책을 반납하면 대여는 RETURN 상태가 된다.")
    @Test
    void returnStatusTest() {
        // when
        rental.returnBy(일반회원1, returnDate);

        // then
        assertThat(rental.isReturned()).isTrue();
    }

    @DisplayName("책을 반납하면 책의 재고가 하나 늘어나야 한다.")
    @Test
    void increaseBookStockTest() {
        // when
        rental.returnBy(일반회원1, returnDate);

        // then
        assertThat(book.getStockQuantity()).isEqualTo(StockQuantity.valueOf(INITIAL_BOOK_STOCK));
    }

    @DisplayName("책을 반납하면 반납한 시간이 기록된다.")
    @Test
    void recordReturnDateTest() {
        // when
        rental.returnBy(일반회원1, returnDate);

        // then
        assertThat(rental.getReturnDate()).isEqualTo(returnDate);
    }
}
