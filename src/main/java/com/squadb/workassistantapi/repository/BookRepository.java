package com.squadb.workassistantapi.repository;

import com.squadb.workassistantapi.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
