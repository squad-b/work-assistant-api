package com.squadb.workassistantapi.domain;

public enum MemberType {
    NORMAL,
    ADMIN;

    public boolean isAdmin() {
        return this.equals(ADMIN);
    }
}
