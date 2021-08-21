package com.squadb.workassistantapi.reservation.domain;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;

public interface ReservationValidator {

    void canReserve(Member member, Book book);

    void notExistsOtherMemberReservation(Book book, Member member);
}
