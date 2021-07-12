package com.squadb.workassistantapi.repository;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Rental;
import com.squadb.workassistantapi.domain.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByBook(Book book);
    List<Rental> findAllByMember(Member member);
    List<Rental> findAllByMemberAndStatus(Member member, RentalStatus rentalStatus);
}
