package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.*;
import com.squadb.workassistantapi.domain.exceptions.NotRentableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceUnitTest {

    @InjectMocks
    RentalService rentalService;

    @Mock
    MemberService memberService;

    @Mock
    BookService bookService;

    @Mock
    RentalRepository rentalRepository;

    @Mock
    ReservationRepository reservationRepository;

    @Test
    @DisplayName("재고가 있고 예약회원이 없다면 대여할 수 있다.")
    public void test() throws Exception {
        //given
        Member member = mock(Member.class);
        Book book = mock(Book.class);
        given(book.canRental()).willReturn(false);

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(member, book);

        given(memberService.findById(any())).willReturn(member);
        given(bookService.findById(any())).willReturn(book);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findWaitingReservationWithMemberByBookId(any());
        doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(rentalRepository)
                .save(any(Rental.class));

        //when
        Executable executable = () -> rentalService.rentBook(book.getId(), member.getId(), false);

        //then
        assertDoesNotThrow(executable);
    }

    @Test
    @DisplayName("예약자가 있는 책은 대여하지 못한다.")
    public void rentBook_bookReservedByOther_ExceptionThrown() throws Exception {
        //given
        Member memberA = mock(Member.class);
        Member memberB = mock(Member.class);
        Book book = mock(Book.class);
        given(book.canRental()).willReturn(false);

        /* memberA가 예약을 한 상태 */
        Reservation reservation = Reservation.createReservation(memberA, book);

        given(bookService.findById(any())).willReturn(book);
        given(memberService.findById(any())).willReturn(memberB);
        doReturn(Optional.of(reservation))
                .when(reservationRepository)
                .findWaitingReservationWithMemberByBookId(any());
        doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(rentalRepository)
                .save(any(Rental.class));

        //when
        /* memberA가 미리 예약한 책을 memberB가 대여 요청 */
        Executable executable = () -> rentalService.rentBook(book.getId(), memberB.getId(), false);

        //then
        assertThrows(NotRentableException.class, executable);
    }
}
