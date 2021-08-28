package com.squadb.workassistantapi.reservation.domain;


import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;

public interface ReservationValidator {

    void validateCanReserve(Member member, Book book);

    void notExistsOtherMemberReservation(Book book, Member member);
}
