package com.squadb.workassistantapi.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
public class Member {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType type;

    public static Member createMember(String email, String passwordHash, MemberType memberType) {
        Member member = new Member();
        member.email = email;
        member.passwordHash = passwordHash;
        member.type = memberType;
        return member;
    }

    public boolean isAdmin() {
        return type.isAdmin();
    }
}
