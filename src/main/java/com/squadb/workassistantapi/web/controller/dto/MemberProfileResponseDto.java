package com.squadb.workassistantapi.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileResponseDto {

    private String result;
    private String email;
    private String name;
    private MemberType memberType;

    public static MemberProfileResponseDto success(Member member) {
        final MemberProfileResponseDto response = new MemberProfileResponseDto();
        response.result = "SUCCESS";
        response.email = member.getEmail();
        response.name = member.getName();
        response.memberType = member.getType();
        return response;
    }

    public static MemberProfileResponseDto fail(String result) {
        final MemberProfileResponseDto response = new MemberProfileResponseDto();
        response.result = result;
        return response;
    }
}
