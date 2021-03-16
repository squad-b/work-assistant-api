package com.squadb.workassistantapi.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Getter
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
