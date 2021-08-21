package com.squadb.workassistantapi.member.dto;

import com.squadb.workassistantapi.member.domain.MemberType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequestDto {
    private String email;
    private String name;
    private String password;
    private MemberType memberType;
}
