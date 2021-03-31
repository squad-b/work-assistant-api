package com.squadb.workassistantapi.repository;

import com.squadb.workassistantapi.domain.Book;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByBook(Book book);
    List<Rental> findAllByMember(Member member);
}
