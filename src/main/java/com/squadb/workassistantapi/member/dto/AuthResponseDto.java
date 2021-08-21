package com.squadb.workassistantapi.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class AuthResponseDto {
    private String result;
    private LoginMember loginMember;

    public static AuthResponseDto success(LoginMember loginMember) {
        final AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.loginMember = loginMember;
        authResponseDto.result = "SUCCESS";
        return authResponseDto;
    }

    public static AuthResponseDto fail(String result) {
        final AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.result = result;
        return authResponseDto;
    }
}
