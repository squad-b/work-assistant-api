package com.squadb.workassistantapi.web.controller.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String email;
    private String password;

    public static LoginRequestDto of(String email, String password) {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.email = email;
        loginRequestDto.password = password;
        return loginRequestDto;
    }
}
