package com.squadb.workassistantapi.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;
import com.squadb.workassistantapi.web.exception.LoginFailedException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id", "type"})
public class LoginMember {
    private static final String SESSION_KEY = "LOGIN_MEMBER";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter private long id;
    @Getter private MemberType type;

    public static LoginMember putInSession(Member member, HttpSession httpSession) {
        try {
            LoginMember loginMember = new LoginMember(member.getId(), member.getType());
            httpSession.setAttribute(LoginMember.SESSION_KEY, objectMapper.writeValueAsString(loginMember));
            return loginMember;
        } catch (JsonProcessingException e) {
            throw LoginFailedException.failSaveInSession();
        }
    }

    public static LoginMember getFromSession(HttpSession httpSession) {
        String loginMemberJson = (String) httpSession.getAttribute(SESSION_KEY);
        if (loginMemberJson == null) { return null; }
        try {
            return objectMapper.readValue(loginMemberJson, LoginMember.class);
        } catch (JsonProcessingException e) {
            log.error("login member error: ", e);
            throw new IllegalStateException(e);
        }
    }

    @JsonIgnore
    public boolean isAdmin() { return type.isAdmin(); }

    @JsonIgnore
    public boolean is(Member member) {
        return member != null && member.getId() == this.id;
    }
}
