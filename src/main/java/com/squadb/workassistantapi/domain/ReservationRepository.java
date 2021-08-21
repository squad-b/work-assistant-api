package com.squadb.workassistantapi.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositorySupport{

    @EntityGraph(attributePaths = {"member"})
    Optional<Reservation> findReservationWithMemberById(Long reservationId);
}
