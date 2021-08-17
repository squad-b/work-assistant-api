package com.squadb.workassistantapi.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // TODO: [2021/08/16 양동혁] Querydsl로 중복쿼리 제거하자
    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.member " +
            "WHERE r.status = com.squadb.workassistantapi.domain.ReservationStatus.WAITING " +
            "AND r.book.id = :bookId")
    Optional<Reservation> findWaitingReservationWithMemberByBookId(@Param("bookId") Long bookId);

    @EntityGraph(attributePaths = {"member"})
    Optional<Reservation> findReservationWithMemberById(Long reservationId);

    /**
     * @return 대여가 가능하지만 아직 대여를 하지 않은 예약
     */
    @Query("SELECT r FROM Reservation r " +
            "JOIN Book b ON r.book = b " +
            "WHERE r.status = com.squadb.workassistantapi.domain.ReservationStatus.WAITING " +
            "AND b.stockQuantity.value > 1")
    List<Reservation> findRentableReservation();
}
