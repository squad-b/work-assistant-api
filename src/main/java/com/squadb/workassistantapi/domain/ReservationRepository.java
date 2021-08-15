package com.squadb.workassistantapi.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"member"})
    Optional<Reservation> findReservationWithMemberByBookIdAndStatus(Long bookId, ReservationStatus status);

    @EntityGraph(attributePaths = {"member"})
    Optional<Reservation> findReservationWithMemberById(Long reservationId);
}
