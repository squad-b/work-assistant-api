package com.squadb.workassistantapi.reservation.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;

public interface ReservationFinisher {

    void finish(Book book, Member member);
}
