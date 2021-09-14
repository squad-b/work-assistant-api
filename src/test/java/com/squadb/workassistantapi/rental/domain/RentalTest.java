package com.squadb.workassistantapi.rental.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.book.domain.StockQuantity;
import com.squadb.workassistantapi.reservation.domain.ReservationFinisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.squadb.workassistantapi.book.domain.IsbnTest.isbn;
import static com.squadb.workassistantapi.member.domain.MemberTest.관리자;
import static com.squadb.workassistantapi.member.domain.MemberTest.일반회원1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RentalTest {

    private Rental rental;
    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .registrant(관리자)
                .title("JPA")
                .isbn(isbn)
                .stockQuantity(StockQuantity.valueOf(2))
                .registrationDate(LocalDateTime.now())
                .build();

        final LocalDateTime rentalStartDate = LocalDateTime.now();

        rental = Rental.createRental(book, 일반회원1, false, rentalStartDate, mock(RentalValidator.class), mock(ReservationFinisher.class));
    }

    @DisplayName("책 대여시 책의 재고가 하나 줄어야 한다.")
    @Test
    void createRentalTest() {
        // then
        assertThat(book.getStockQuantity()).isEqualTo(StockQuantity.valueOf(1));
    }

    @DisplayName("일반회원은 자신이 빌린 책을 반납할 수 있다.")
    @Test
    void normalMemberBookReturnTest() {

    }

    @DisplayName("일반회원은 다른사람이 빌린 책을 반납할 수 없다.")
    @Test
    void normalMemberBookReturnFailTest() {

    }

    @DisplayName("관리자는 다른사람이 빌린 책도 반납할 수 있다.")
    @Test
    void adminMemberBookReturnTest() {

    }

    @DisplayName("책을 반납하면 대여는 RETURN 상태가 된다.")
    @Test
    void returnStatusTest() {

    }

    @DisplayName("책을 반납하면 책의 재고가 하나 늘어나야 한다.")
    @Test
    void increaseBookStockTest() {

    }

    @DisplayName("책을 반납하면 반납한 시간이 기록된다.")
    @Test
    void recordReturnDateTest() {

    }
}
