package com.squadb.workassistantapi.web.controller.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
