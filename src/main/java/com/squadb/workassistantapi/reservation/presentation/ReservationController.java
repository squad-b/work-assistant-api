package com.squadb.workassistantapi.reservation.presentation;

import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.member.infrastructure.config.CheckPermission;
import com.squadb.workassistantapi.member.infrastructure.config.CurrentLoginMember;
import com.squadb.workassistantapi.reservation.application.ReservationService;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationException;
import com.squadb.workassistantapi.reservation.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @CheckPermission
    @GetMapping(path = "/reservations", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<List<ReservationResponseDto>> findAllReservation(@RequestBody ReservationSearchAllDto reservationSearchAllDto,
                                                                              @PageableDefault(size = 20) Pageable pageable) {
        Page<Reservation> pageResult = reservationService.findAllReservation(reservationSearchAllDto, pageable);
        PaginationResponseDto paginationResponseDto = PaginationResponseDto.from(pageResult);
        List<ReservationResponseDto> reservationResponseDtoList = ReservationResponseDto.list(pageResult);
        return CommonResponseDto.ok(reservationResponseDtoList, paginationResponseDto);
    }

    @GetMapping(path = "/members/reservations",  consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<List<ReservationResponseDto>> findMyReservation(@CurrentLoginMember LoginMember loginMember,
                                                                             @PageableDefault Pageable pageable) {
        Page<Reservation> pageResult = reservationService.findMyReservation(loginMember.getId(), pageable);
        PaginationResponseDto paginationResponseDto = PaginationResponseDto.from(pageResult);
        List<ReservationResponseDto> reservationResponseDtoList = ReservationResponseDto.list(pageResult);
        return CommonResponseDto.ok(reservationResponseDtoList, paginationResponseDto);
    }

    @PostMapping(path = "/reservations", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<ReservationIdResponseDto> reserve(@RequestBody ReservationRequestDto reservationRequestDto,
                                                               @CurrentLoginMember LoginMember loginMember) {
        Long reservationId = reservationService.reserve(reservationRequestDto.getBookId(), loginMember.getId());
        return CommonResponseDto.ok(new ReservationIdResponseDto(reservationId));
    }

    @DeleteMapping(path = "/reservations/{reservationId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> cancel(@PathVariable(value = "reservationId") long reservationId,
                                 @CurrentLoginMember LoginMember loginMember) {
        reservationService.cancel(reservationId, loginMember.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ReservationException.class)
    public CommonResponseDto<String> handleReservationException(ReservationException e) {
        log.error("{}", e.getMessage(), e);
        return CommonResponseDto.fail(e.getMessage());
    }
}
