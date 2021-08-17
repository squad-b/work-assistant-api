package com.squadb.workassistantapi.web.controller.batch;

import com.squadb.workassistantapi.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReservationBatchController {

    private final ReservationService reservationService;

    // TODO: [2021/08/16 양동혁] 통합테스트
    @Scheduled(cron = "0 0 0 * * *")
    public void cancelExpiredReservation() {
        long revokedReservationCount = reservationService.revokeExpiredReservation();
        log.info("{} reservations have been revoked.", revokedReservationCount);
    }
}
