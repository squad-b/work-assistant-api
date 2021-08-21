package com.squadb.workassistantapi.rental.domain;

import com.squadb.workassistantapi.book.domain.Book;
import com.squadb.workassistantapi.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByBook(Book book);
    List<Rental> findAllByMember(Member member);
    List<Rental> findAllByMemberAndStatus(Member member, RentalStatus rentalStatus);
}
