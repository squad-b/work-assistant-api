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
import static org.mockito.ArgumentMatchers.anyLong;
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


    private static final Long MEMBER_ID = 1L;

    private static final Long BOOK_ID = 1L;

    @Test
    @DisplayName("예약자가 있는 책은 예약할 수 없다.")
    public void reserve_alreadyReservedBook_ExceptionThrown() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member memberA = MemberTest.createMember(MemberType.NORMAL);
        Member memberB = MemberTest.createMember(MemberType.NORMAL);

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(memberA, book);

        /* 이미 예약된책을 memberB가 예약 요청 */
        given(memberService.findById(anyLong())).willReturn(memberB);

        given(bookService.findById(anyLong())).willReturn(book);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findReservationWithMemberByBookIdAndStatus(any(), any(ReservationStatus.class));

        //when
        Executable executable = () -> reservationService.reserve(MEMBER_ID, BOOK_ID);
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

        /* 동일한 회원이 같은 책을 여러번 에약 요청 */
        given(memberService.findById(anyLong())).willReturn(member);

        given(bookService.findById(anyLong())).willReturn(book);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findReservationWithMemberByBookIdAndStatus(any(), any(ReservationStatus.class));

        // TODO: [2021/08/15 양동혁] refact: memberId, bookId
        //when
        Executable executable = () -> reservationService.reserve(MEMBER_ID, BOOK_ID);
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.ALREADY_MYSELF_RESERVED);
    }

    // TODO: [2021/08/15 양동혁] refact: 도메인 계층 테스트로 옮기자 
    @Test
    @DisplayName("다른회원의 예약은 취소할 수 없다.")
    public void cancel_notAuthorizedReservation_ExceptionThrown() throws Exception {
        //given
        Book book = BookFactory.createBookOutOfStockRegisteredBy(MemberTest.관리자);
        Member memberA = MemberTest.createMember(MemberType.NORMAL);
        Member memberB = MemberTest.createMember(MemberType.NORMAL);

        doReturn(memberB)
                .when(memberService)
                .findById(any());

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(memberA, book);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findReservationWithMemberById(any());

        //when
        /* memberA의 예약을 memberB가 취소 요청 */
        Executable executable = () -> reservationService.cancel(reservation.getId(), memberB.getId());
        ReservationException resultException = assertThrows(ReservationException.class, executable);

        //then
        ReservationErrorCode resultErrorCode = resultException.getErrorCode();
        assertThat(resultErrorCode).isEqualTo(ReservationErrorCode.NOT_AUTHORIZED);
    }
}