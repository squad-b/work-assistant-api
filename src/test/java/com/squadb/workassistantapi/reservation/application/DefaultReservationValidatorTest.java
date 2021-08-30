package com.squadb.workassistantapi.reservation.application;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationErrorCode;
import com.squadb.workassistantapi.reservation.domain.ReservationException;
import com.squadb.workassistantapi.reservation.domain.ReservationRepository;
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
    @DisplayName("대여할 수 있는 책을 예약하면 예외를 던진다.")
    public void reserve_BookInStock_ExceptionThrown() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = getBookInStock();
        Reservation reservation = mock(Reservation.class);
        given(reservation.isReservedBy(any())).willReturn(false);
        willReturn(List.of(reservation))
                .given(reservationRepository)
                .findAllByBookIdAndStatus(any(), any());

        //when
        Executable executable = () -> reservationValidator.validateCanReserve(member, book);

        //then
        verifyThrowsException(executable, ReservationErrorCode.NOT_RESERVABLE);
    }

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
        Executable executable = () -> reservationValidator.validateCanReserve(member, book);

        //then
        verifyThrowsException(executable, ReservationErrorCode.ALREADY_MYSELF_RESERVED);
    }

    private void verifyThrowsException(Executable executable, ReservationErrorCode expectedErrorCode) {
        ReservationException resultException = assertThrows(ReservationException.class, executable);
        ReservationErrorCode errorCode = resultException.getErrorCode();
        assertThat(errorCode).isEqualTo(expectedErrorCode);
    }

    private Book getBookInStock() {
        Book book = mock(Book.class);
        given(book.isOutOfStock()).willReturn(false);
        return book;
    }
}
