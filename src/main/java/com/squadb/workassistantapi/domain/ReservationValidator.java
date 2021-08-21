package com.squadb.workassistantapi.domain;

public interface ReservationValidator {

    void canReserve(Member member, Book book);

    void notExistsOtherMemberReservation(Book book, Member member);
}
