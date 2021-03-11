package com.squadb.workassistantapi.repository;

import com.squadb.workassistantapi.domain.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
