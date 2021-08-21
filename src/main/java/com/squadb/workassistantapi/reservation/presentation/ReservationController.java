package com.squadb.workassistantapi.reservation.presentation;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.squadb.workassistantapi.reservation.application.ReservationService;
import com.squadb.workassistantapi.reservation.domain.Reservation;
import com.squadb.workassistantapi.reservation.domain.ReservationException;
import com.squadb.workassistantapi.reservation.dto.*;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.interceptor.CheckPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @CheckPermission
    @GetMapping(path = "/admin", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<List<ReservationResponseDto>> findAllReservation(@RequestBody ReservationSearchAllDto reservationSearchAllDto,
                                                                              @PageableDefault(size = 20) Pageable pageable) {
        Page<Reservation> pageResult = reservationService.findAllReservation(reservationSearchAllDto, pageable);
        PaginationResponseDto paginationResponseDto = convertToPaginationResponseDto(pageResult);
        List<ReservationResponseDto> reservationResponseDtoList = convertToReservationResponseDtoList(pageResult);
        return CommonResponseDto.ok(reservationResponseDtoList, paginationResponseDto);
    }

    private List<ReservationResponseDto> convertToReservationResponseDtoList(Page<Reservation> pageResult) {
        return pageResult.getContent().stream()
                .map(ReservationResponseDto::of)
                .collect(Collectors.toList());
    }

    private PaginationResponseDto convertToPaginationResponseDto(Page<Reservation> pageResult) {
        return PaginationResponseDto.builder()
                .page(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .pageCount(pageResult.getTotalPages())
                .totalCount(pageResult.getTotalElements())
                .build();
    }

    @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<List<ReservationResponseDto>> findMyReservation(@CurrentLoginMember LoginMember loginMember,
                                                                             @PageableDefault Pageable pageable) {
        Page<Reservation> pageResult = reservationService.findMyReservation(loginMember.getId(), pageable);
        PaginationResponseDto paginationResponseDto = convertToPaginationResponseDto(pageResult);
        List<ReservationResponseDto> reservationResponseDtoList = convertToReservationResponseDtoList(pageResult);
        return CommonResponseDto.ok(reservationResponseDtoList, paginationResponseDto);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<ReservationIdResponseDto> reserve(@RequestBody ReservationRequestDto reservationRequestDto,
                                                               @CurrentLoginMember LoginMember loginMember) {
        Long reservationId = reservationService.reserve(reservationRequestDto.getBookId(), loginMember.getId());
        return CommonResponseDto.ok(new ReservationIdResponseDto(reservationId));
    }

    @DeleteMapping(path = "/{reservationId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<ReservationIdResponseDto> cancel(@PathVariable(value = "reservationId") long reservationId,
                                                              @CurrentLoginMember LoginMember loginMember) {
        reservationService.cancel(reservationId, loginMember.getId());
        return CommonResponseDto.ok(new ReservationIdResponseDto(reservationId));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ReservationException.class)
    public CommonResponseDto<String> handleReservationException(ReservationException e) {
        return CommonResponseDto.fail(e.getMessage());
    }

    //handle jackson enum databind
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ValueInstantiationException.class)
    public CommonResponseDto<String> handleValueInstantiationException() {
        return CommonResponseDto.fail("지원하지 않는 형식입니다.");
    }
}
