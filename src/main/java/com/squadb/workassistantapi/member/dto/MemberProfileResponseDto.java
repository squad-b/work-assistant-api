package com.squadb.workassistantapi.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.domain.MemberType;
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
