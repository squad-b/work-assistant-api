package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        ReservationException resultException = assertThrows(ReservationException.class, executable);

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
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.REQUIRED_RESERVATION);
    }

    @Test
    @DisplayName("대기 중인 예약만 취소할 수 있다.")
    public void cancel_NotWaitingReservation_ExceptionThrown() throws Exception {
        //given
        Member member = MemberTest.관리자;
        Book book = BookFactory.createBookOutOfStockRegisteredBy(member);
        Reservation reservation = Reservation.createReservation(member, book);

        /* 예약 취소 */
        reservation.cancelBy(member);

        //when
        /* 취소한 예약을 다시 취소 요청 */
        Executable executable = () -> reservation.cancelBy(member);
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ILLEGAL_STATUS);
    }

    @Test
    @DisplayName("다른회원의 예약은 취소할 수 없다.")
    public void cancel_notAuthorizedReservation_ExceptionThrown() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member memberA = MemberTest.createMember(MemberType.NORMAL);
        Member memberB = MemberTest.createMember(MemberType.NORMAL);

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(memberA, book);

        //when
        /* memberA의 예약을 memberB가 취소 요청 */
        Executable executable = () -> reservation.cancelBy(memberB);
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("만료날짜를 지난 예약은 취소된다.")
    public void revoke_expiredReservation() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member member = MemberTest.createMember(MemberType.NORMAL);

        //when
        Reservation reservation = Reservation.createReservation(member, book);
        boolean isRevoked = reservation.revokeReservationExpiringOn(LocalDateTime.now().plusDays(3));

        //then
        assertThat(isRevoked).isTrue();
    }

    @Test
    @DisplayName("만료날짜를 지나지 않은 예약은 취소되지 않는다.")
    public void revoke_notExpiredReservation() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member member = MemberTest.createMember(MemberType.NORMAL);

        //when
        Reservation reservation = Reservation.createReservation(member, book);
        boolean isRevoked = reservation.revokeReservationExpiringOn(LocalDateTime.now().plusDays(2));

        //then
        assertThat(isRevoked).isFalse();
    }
}