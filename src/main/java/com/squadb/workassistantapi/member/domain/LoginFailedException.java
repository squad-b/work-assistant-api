package com.squadb.workassistantapi.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginFailedException extends RuntimeException {
    @Getter
    private final String result;

    public static LoginFailedException noSuchMember() {
        return new LoginFailedException("NOT_FOUND");
    }

    public static LoginFailedException wrongPassword() { return new LoginFailedException("WRONG_PASSWORD"); }
}
