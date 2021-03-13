package com.squadb.workassistantapi.domain;

public enum MemberType {
    NORMAL,
    ADMIN;

    boolean isAdmin() {
        return this.equals(ADMIN);
    }
}
