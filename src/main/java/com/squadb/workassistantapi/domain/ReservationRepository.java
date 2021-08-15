package com.squadb.workassistantapi.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findReservationWithMemberByBookIdAndStatus(Long bookId, ReservationStatus status);
}
