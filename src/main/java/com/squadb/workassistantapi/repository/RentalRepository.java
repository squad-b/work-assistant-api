package com.squadb.workassistantapi.repository;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByBook(Book book);

    @Query("select r from Rental r where r.member.id = ?1")
    List<Rental> findAllByMemberId(Long memberId);
}
