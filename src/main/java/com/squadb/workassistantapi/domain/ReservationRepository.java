package com.squadb.workassistantapi.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositorySupport{

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.book " +
            "WHERE r.member.id = :memberId " +
            "AND r.status = com.squadb.workassistantapi.domain.ReservationStatus.WAITING")
    List<Reservation> findAllWaitingReservationByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.member " +
            "WHERE r.book.id = :bookId " +
            "AND r.status = com.squadb.workassistantapi.domain.ReservationStatus.WAITING")
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
