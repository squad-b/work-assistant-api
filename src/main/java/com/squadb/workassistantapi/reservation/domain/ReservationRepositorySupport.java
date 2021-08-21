package com.squadb.workassistantapi.reservation.domain;

import com.squadb.workassistantapi.reservation.dto.ReservationSearchAllDto;
import com.squadb.workassistantapi.reservation.dto.ReservationSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReservationRepositorySupport {

    Page<Reservation> findAllBySearchAll(ReservationSearchAllDto reservationSearchAllDto, Pageable pageable);

    Page<Reservation> findAllByMemberIdAndStatus(Long memberId, ReservationStatus status, Pageable pageable);

    List<Reservation> findAllByMemberIdAndStatus(Long memberId, ReservationStatus status);

    List<Reservation> findAllByBookIdAndStatus(Long bookId, ReservationStatus status);

    List<Reservation> findAllWithBookByStatus(ReservationStatus reservationStatus);

    Optional<Reservation> findReservationWithMemberBySearch(ReservationSearchDto reservationSearchDto);
}
