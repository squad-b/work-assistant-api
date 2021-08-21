package com.squadb.workassistantapi.member.domain;

public enum MemberType {
    NORMAL,
    ADMIN;

    public boolean isAdmin() {
        return this.equals(ADMIN);
    }
}
