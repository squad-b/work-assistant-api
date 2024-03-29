package com.squadb.workassistantapi.reservation.presentation;

import com.squadb.workassistantapi.reservation.application.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReservationBatchController {

    private final ReservationService reservationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void cancelExpiredReservation() {
        long revokedReservationCount = reservationService.revokeExpiredReservation();
        log.info("{} reservations have been revoked.", revokedReservationCount);
    }
}
