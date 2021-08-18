package com.squadb.workassistantapi.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.squadb.workassistantapi.domain.IsbnTest.isbn;
import static com.squadb.workassistantapi.domain.MemberTest.관리자;
import static com.squadb.workassistantapi.domain.MemberTest.일반회원;
import static org.assertj.core.api.Assertions.assertThat;

class RentalTest {

    @DisplayName("책 대여시 책의 재고가 하나 줄어야 한다.")
    @Test
    void createRentalTest() {
        // given
        final Book springBook = Book.builder()
                .registrant(관리자)
                .title("JPA")
                .isbn(isbn)
                .stockQuantity(StockQuantity.valueOf(2))
                .registrationDate(LocalDateTime.now())
                .build();
        final LocalDateTime rentalStartDate = LocalDateTime.now();

        // when
        Rental.createRental(springBook, 일반회원, false, rentalStartDate);

        // then
        assertThat(springBook.getStockQuantity()).isEqualTo(StockQuantity.valueOf(1));
    }

}
