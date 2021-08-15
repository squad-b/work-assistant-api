package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.domain.exceptions.ReservationErrorCode;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doReturn;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    MemberService memberService;

    @Mock
    BookService bookService;

    @Mock
    ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약자가 있는 책은 예약할 수 없다.")
    public void reserve_alreadyReservedBook_ExceptionThrown() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member memberA = MemberTest.createMember(MemberType.NORMAL);
        Member memberB = MemberTest.createMember(MemberType.NORMAL);

        given(memberService.findById(any())).willReturn(memberB);
        given(bookService.findById(any())).willReturn(book);

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(memberA, book);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findReservationWithMemberByBookIdAndStatus(any(), any(ReservationStatus.class));

        //when
        /* 이미 예약된 책을 memberB가 예약 요청 */
        Executable executable = () -> reservationService.reserve(memberB.getId(), book.getId());
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ALREADY_RESERVED);
    }

    @Test
    @DisplayName("동일한 회원이 같은 책을 여러번 예약할 수 없다.")
    public void reserve_alreadyMyselfReservedBook_ExceptionThrown() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member member = MemberTest.createMember(MemberType.NORMAL);
        Reservation reservation = Reservation.createReservation(member, book);

        given(memberService.findById(any())).willReturn(member);
        given(bookService.findById(any())).willReturn(book);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findReservationWithMemberByBookIdAndStatus(any(), any(ReservationStatus.class));

        //when
        /* 동일한 회원이 같은 책을 여러번 에약 요청 */
        Executable executable = () -> reservationService.reserve(member.getId(), book.getId());
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ALREADY_MYSELF_RESERVED);
    }
}