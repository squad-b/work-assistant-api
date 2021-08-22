package com.squadb.workassistantapi.reservation.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

class ReservationTest {

    private ReservationValidator mockReservationValidator;

    @BeforeEach
    private void beforeEach() {
        mockReservationValidator = mock(ReservationValidator.class);
    }

    @Test
    @DisplayName("예약시 상태는 '대기중' 이다.")
    public void reserve_success_waitingStatus() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(true);

        //when
        Reservation reservation = Reservation.createReservation(member, book, mockReservationValidator);

        //then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    @DisplayName("대여할 수 있는 책은 예약할 수 없다.")
    public void reserve_BookInStock_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(false);

        //when
        Executable executable = () -> Reservation.createReservation(member, book, mockReservationValidator);

        //then
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.NOT_RESERVABLE);
    }

    @Test
    @DisplayName("회원과 책은 필수정보이다.")
    public void createReservation_NullParameter_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = null;

        //when
        Executable executable = () -> Reservation.createReservation(member, book, mockReservationValidator);

        //then
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.REQUIRED_RESERVATION);
    }

    @Test
    @DisplayName("대기 중인 예약만 취소할 수 있다.")
    public void cancel_NotWaitingReservation_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(true);
        Reservation reservation = Reservation.createReservation(member, book, mockReservationValidator);

        /* 예약 취소 */
        reservation.cancelBy(member);

        //when
        /* 취소한 예약을 다시 취소 요청 */
        Executable executable = () -> reservation.cancelBy(member);

        //then
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ILLEGAL_STATUS);
    }

    @Test
    @DisplayName("다른회원의 예약은 취소할 수 없다.")
    public void cancel_notAuthorizedReservation_ExceptionThrown() throws Exception {
        //given
        Member memberA = mock(Member.class);
        Member memberB = mock(Member.class);
        Book book = getMockBook(true);

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(memberA, book, mockReservationValidator);

        //when
        /* memberA의 예약을 memberB가 취소 요청 */
        Executable executable = () -> reservation.cancelBy(memberB);

        //then
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("만료날짜를 지난 예약은 취소된다.")
    public void revoke_expiredReservation() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(true);
        Reservation reservation = Reservation.createReservation(member, book, mockReservationValidator);

        //when
        boolean isRevoked = reservation.revokeReservationExpiringOn(LocalDateTime.now().plusDays(3));

        //then
        assertThat(isRevoked).isTrue();
    }

    @Test
    @DisplayName("만료날짜를 지나지 않은 예약은 취소되지 않는다.")
    public void revoke_notExpiredReservation() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(true);

        //when
        Reservation reservation = Reservation.createReservation(member, book, mockReservationValidator);
        boolean isRevoked = reservation.revokeReservationExpiringOn(LocalDateTime.now().plusDays(2));

        //then
        assertThat(isRevoked).isFalse();
    }

    @Test
    @DisplayName("예약자가 있는 책은 예약할 수 없다.")
    public void reserve_alreadyReservedBook_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(true);

        willThrow(new ReservationException(ReservationErrorCode.ALREADY_RESERVED))
                .given(mockReservationValidator)
                .canReserve(member, book);

        //when
        Executable executable = () -> Reservation.createReservation(member, book, mockReservationValidator);

        //then
        ReservationException reservationException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode resultErrorCode = reservationException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ALREADY_RESERVED);
    }

    @Test
    @DisplayName("동일한 회원이 같은 책을 여러번 예약할 수 없다.")
    public void reserve_alreadyMyselfReservedBook_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getMockBook(true);

        willThrow(new ReservationException(ReservationErrorCode.ALREADY_MYSELF_RESERVED))
                .given(mockReservationValidator)
                .canReserve(member, book);

        //when
        Executable executable = () -> Reservation.createReservation(member, book, mockReservationValidator);

        //then
        ReservationException reservationException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode resultErrorCode = reservationException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ALREADY_MYSELF_RESERVED);
    }

    private Book getMockBook(boolean isOutOfStock) {
        Book book = mock(Book.class);
        given(book.isOutOfStock()).willReturn(isOutOfStock);
        return book;
    }
}