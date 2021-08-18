package com.squadb.workassistantapi.domain;

public interface PasswordEncryptor {
    String encrypt(String plain);

    boolean match(String password, String plain);
}
