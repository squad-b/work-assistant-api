package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Reservation;
import com.squadb.workassistantapi.domain.ReservationRepository;
import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DefaultReservationValidatorTest {

    @InjectMocks
    DefaultReservationValidator reservationValidator;

    @Mock
    ReservationRepository reservationRepository;

    @Test
    @DisplayName("같은 회원이 이미 예약한 책을 다시 예약히면 예외를 던진다.")
    public void reserve_alreadyReservedBookMyself_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = mock(Book.class);

        Reservation reservation = mock(Reservation.class);
        given(reservation.isReservedBy(any())).willReturn(true);
        willReturn(List.of(reservation))
                .given(reservationRepository)
                .findAllByBookIdAndStatus(any(), any());

        //when
        Executable executable = () -> reservationValidator.canReserve(member, book);

        //then
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode errorCode = resultException.getErrorCode();
        assertThat(errorCode).isEqualTo(ReservationErrorCode.ALREADY_MYSELF_RESERVED);
    }

    @Test
    @DisplayName("예약자가 있는 책에 대여신청을 하면 예외를 던진다.")
    public void reserve_alreadyReservedBookByOther_ExceptionThrown () throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = mock(Book.class);

        Reservation reservation = mock(Reservation.class);
        given(reservation.isReservedBy(any())).willReturn(false);
        willReturn(List.of(reservation))
                .given(reservationRepository)
                .findAllByBookIdAndStatus(any(), any());

        //when
        Executable executable = () -> reservationValidator.notExistsOtherMemberReservation(book, member);

        //then
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode errorCode = resultException.getErrorCode();
        assertThat(errorCode).isEqualTo(ReservationErrorCode.ALREADY_RESERVED);
    }
}