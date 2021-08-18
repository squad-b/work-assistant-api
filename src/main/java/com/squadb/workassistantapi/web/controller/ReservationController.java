package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import com.squadb.workassistantapi.service.ReservationService;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.controller.dto.ReservationRequestDto;
import com.squadb.workassistantapi.web.controller.dto.ReservationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ReservationResponseDto reserve(@RequestBody ReservationRequestDto reservationRequestDto,
                                          @CurrentLoginMember LoginMember loginMember) {
        reservationRequestDto.validateNotNull();
        Long reservationId = reservationService.reserve(reservationRequestDto.getBookId(), loginMember.getId());
        return ReservationResponseDto.ok(reservationId);
    }

    @DeleteMapping(path = "/{reservationId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ReservationResponseDto cancel(@PathVariable(value = "reservationId") Optional<Long> optionalReservationId,
                                         @CurrentLoginMember LoginMember loginMember) {
        Long reservationId = optionalReservationId.orElseThrow(IllegalArgumentException::new);
        reservationService.cancel(reservationId, loginMember.getId());
        return ReservationResponseDto.ok(reservationId);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ReservationException.class)
    public ReservationResponseDto handleReservationException(ReservationException e) {
        return ReservationResponseDto.fail(e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ReservationResponseDto handleReservationException() {
        return ReservationResponseDto.fail("입력 값이 유효하지 않습니다.");
    }
}
