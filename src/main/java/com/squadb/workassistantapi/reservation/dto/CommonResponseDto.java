package com.squadb.workassistantapi.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class CommonResponseDto<T> {

    private T response;
    private String errorMessage;
    private PaginationResponseDto paginationResponseDto;

    public static <T> CommonResponseDto<T> ok(T response) {
        return new CommonResponseDto<>(response, null, null);
    }

    public static <T> CommonResponseDto<T> ok(T response, PaginationResponseDto paginationResponseDto) {
        return new CommonResponseDto<>(response, null, paginationResponseDto);
    }

    public static <T> CommonResponseDto<T> fail(String errorMessage) {
        return new CommonResponseDto<>(null, errorMessage, null);
    }
}
