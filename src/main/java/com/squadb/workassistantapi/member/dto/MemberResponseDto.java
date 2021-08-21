package com.squadb.workassistantapi.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    private Long memberId;

    public MemberResponseDto(Long memberId) {
        this.memberId = memberId;
    }
}
