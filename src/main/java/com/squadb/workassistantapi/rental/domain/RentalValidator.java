package com.squadb.workassistantapi.rental.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;

public interface RentalValidator {

    void validateNotExistsOtherMemberReservation(Book book, Member member);
}
