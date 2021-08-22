package com.squadb.workassistantapi.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.domain.MemberType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "type"})
public class LoginMember implements Serializable {
    private static final long serialVersionUID = 123456123456L;

    @Getter private Long id;
    @Getter private MemberType type;

    @JsonIgnore
    public boolean isAdmin() { return type.isAdmin(); }

    @JsonIgnore
    public boolean is(Member member) {
        return member != null && member.getId() == this.id;
    }
}
