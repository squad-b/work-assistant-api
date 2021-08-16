package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.util.HashUtil;
import com.squadb.workassistantapi.web.exception.LoginFailedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Getter
    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType type;

    private Member(String email, String name, String passwordHash, MemberType type) {
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.type = type;
    }

    public static Member createMember(String email, String name, String passwordHash, MemberType memberType) {
        return new Member(email, name, passwordHash, memberType);
    }

    public static Member createMember(String email, String name, String plainPassword, PasswordEncryptor passwordEncryptor, MemberType memberType) {
        final String passwordHash = passwordEncryptor.encrypt(plainPassword);
        return new Member(email, name, passwordHash, memberType);
    }

    public boolean isAdmin() {
        return type.isAdmin();
    }

    public void checkEqualPassword(String passwordInput) {
        if (!HashUtil.equalPassword(passwordInput, passwordHash)) {
            throw LoginFailedException.wrongPassword();
        }
    }

    public void checkEqualPassword(String plainPassword, PasswordEncryptor passwordEncryptor) {
        if (!passwordEncryptor.match(passwordHash, plainPassword)) {
            throw LoginFailedException.wrongPassword();
        }
    }

    public void changePassword(String newPassword) {
        this.passwordHash = HashUtil.hashPassword(newPassword);
    }

    public void changePassword(String newPlainPassword, PasswordEncryptor passwordEncryptor) {
        this.passwordHash = passwordEncryptor.encrypt(newPlainPassword);
    }
}
