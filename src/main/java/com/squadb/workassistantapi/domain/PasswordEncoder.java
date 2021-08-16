package com.squadb.workassistantapi.domain;

public interface PasswordEncoder {
    String encode(String plain);

    boolean match(String password, String plain);
}
