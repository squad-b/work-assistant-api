package com.squadb.workassistantapi.reservation.domain;

import com.squadb.workassistantapi.reservation.dto.ReservationSearchAllDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReservationRepositorySupport {

    Page<Reservation> findAllBySearchAll(ReservationSearchAllDto reservationSearchAllDto, Pageable pageable);

    Page<Reservation> findAllByMemberIdAndStatus(Long memberId, ReservationStatus status, Pageable pageable);

    List<Reservation> findAllByMemberIdAndStatus(Long memberId, ReservationStatus status);

    List<Reservation> findAllByBookIdAndStatus(Long bookId, ReservationStatus status);

    List<Reservation> findAllWithBookByStatus(ReservationStatus reservationStatus);
}
