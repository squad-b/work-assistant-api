package com.squadb.workassistantapi.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LoginResponseDto {
    private String result;
    private Long memberId;

    public static LoginResponseDto success(long memberId) {
        final LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.memberId = memberId;
        loginResponseDto.result = "SUCCESS";
        return loginResponseDto;
    }

    public static LoginResponseDto fail(String result) {
        final LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.result = result;
        return loginResponseDto;
    }
}
