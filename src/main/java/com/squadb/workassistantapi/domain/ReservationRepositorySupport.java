package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.web.controller.dto.ReservationSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationRepositorySupport {

    Page<Reservation> findAllReservation(ReservationSearchDto reservationSearchDto, Pageable pageable);
}
