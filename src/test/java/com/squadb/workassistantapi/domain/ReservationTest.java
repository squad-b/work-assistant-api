package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.squadb.workassistantapi.domain.IsbnTest.isbn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    @Test
    @DisplayName("예약시 상태는 '대기중' 이다.")
    public void reserve() throws Exception {
        //given
        Member member = MemberTest.관리자;
        Book book = createBook(member);

        //when
        Reservation reservation = Reservation.createReservation(member, book);

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    @DisplayName("회원과 책은 필수정보이다.")
    public void createReservation_NullParameter_ExceptionThrown() throws Exception {
        Member member = MemberTest.관리자;
        Book book = null;

        assertThatThrownBy(() -> Reservation.createReservation(member, book))
                .isInstanceOf(ReservationException.class);
    }

    private Book createBook(Member registrant) {
        return Book.builder()
                .isbn(isbn)
                .title("Spring")
                .stockQuantity(StockQuantity.valueOf(1))
                .registrationDate(LocalDateTime.now())
                .registrant(registrant)
                .build();
    }
}