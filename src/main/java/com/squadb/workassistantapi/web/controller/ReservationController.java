package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.Reservation;
import com.squadb.workassistantapi.domain.exceptions.ReservationException;
import com.squadb.workassistantapi.service.ReservationService;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.*;
import com.squadb.workassistantapi.web.interceptor.CheckPermission;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    public CommonResponseDto<List<ReservationResponseDto>> findAllReservation(@RequestBody ReservationSearchDto reservationSearchDto,
                                                                              @PageableDefault(size = 20) Pageable pageable) {
        Page<Reservation> pageResult = reservationService.findAllReservation(reservationSearchDto, pageable);
        List<Reservation> reservationList = pageResult.getContent();

        List<ReservationResponseDto> reservationResponseDtoList = reservationList.stream()
                .map(ReservationResponseDto::of)
                .collect(Collectors.toList());
        PaginationResponseDto paginationResponseDto = PaginationResponseDto.builder()
                .page(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .pageCount(pageResult.getTotalPages())
                .totalCount(pageResult.getTotalElements())
                .build();
        return CommonResponseDto.ok(reservationResponseDtoList, paginationResponseDto);
    }

    @GetMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<List<ReservationResponseDto>> findMyReservation(@CurrentLoginMember LoginMember loginMember) {
        List<Reservation> reservationList = reservationService.findAllWaitingReservationByMemberId(loginMember.getId());
        List<ReservationResponseDto> reservationResponseDtoList = reservationList.stream()
                .map(ReservationResponseDto::of)
                .collect(Collectors.toList());
        return CommonResponseDto.ok(reservationResponseDtoList);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<ReservationIdResponse> reserve(@RequestBody ReservationRequestDto reservationRequestDto,
                                                            @CurrentLoginMember LoginMember loginMember) {
        reservationRequestDto.validateNotNull();
        Long reservationId = reservationService.reserve(reservationRequestDto.getBookId(), loginMember.getId());
        return CommonResponseDto.ok(new ReservationIdResponse(reservationId));
    }

    @DeleteMapping(path = "/{reservationId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CommonResponseDto<ReservationIdResponse> cancel(@PathVariable(value = "reservationId") Optional<Long> optionalReservationId,
                                                           @CurrentLoginMember LoginMember loginMember) {
        Long reservationId = optionalReservationId.orElseThrow(IllegalArgumentException::new);
        reservationService.cancel(reservationId, loginMember.getId());
        return CommonResponseDto.ok(new ReservationIdResponse(reservationId));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ReservationException.class)
    public CommonResponseDto<String> handleReservationException(ReservationException e) {
        return CommonResponseDto.fail(e.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public CommonResponseDto<String> handleReservationException() {
        return CommonResponseDto.fail("fail");
    }

    @AllArgsConstructor
    private static class ReservationIdResponse {
        private Long reservationId;
    }
}
