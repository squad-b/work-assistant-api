package com.squadb.workassistantapi.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByBook(Book book);
    List<Rental> findAllByMember(Member member);
    List<Rental> findAllByMemberAndStatus(Member member, RentalStatus rentalStatus);
}
