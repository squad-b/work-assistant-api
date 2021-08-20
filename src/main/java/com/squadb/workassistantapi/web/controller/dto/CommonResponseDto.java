package com.squadb.workassistantapi.web.controller.dto;

import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

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
