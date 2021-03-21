package com.squadb.workassistantapi.web.controller.dto;

import lombok.Getter;

@Getter
public class UpdatePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
