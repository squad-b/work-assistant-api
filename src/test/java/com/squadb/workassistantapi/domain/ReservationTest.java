package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationTest {

    @Test
    @DisplayName("예약시 상태는 '대기중' 이다.")
    public void reserve_success_waitingStatus() throws Exception {
        //given
        Member member = MemberTest.관리자;
        Book book = BookFactory.createBookOutOfStockRegisteredBy(member);

        //when
        Reservation reservation = Reservation.createReservation(member, book);

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    @DisplayName("대여할 수 있는 책은 예약할 수 없다.")
    public void reserve_BookInStock_ExceptionThrown() throws Exception {
        //given
        Member member = MemberTest.관리자;
        Book book = BookFactory.createBookRegisteredBy(member);

        //when
        Executable executable = () -> Reservation.createReservation(member, book);
        ReservationException resultException = Assertions.assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.NOT_RESERVABLE);
    }

    @Test
    @DisplayName("회원과 책은 필수정보이다.")
    public void createReservation_NullParameter_ExceptionThrown() throws Exception {
        //given
        Member member = MemberTest.관리자;
        Book book = null;

        //when
        Executable executable = () -> Reservation.createReservation(member, book);
        ReservationException resultException = Assertions.assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.REQUIRED_RESERVATION);
    }
}