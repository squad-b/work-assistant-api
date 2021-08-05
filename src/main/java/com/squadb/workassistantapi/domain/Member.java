package com.squadb.workassistantapi.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.squadb.workassistantapi.service.exception.PermissionDeniedException;
import com.squadb.workassistantapi.util.HashUtil;
import com.squadb.workassistantapi.web.exception.LoginFailedException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Supplier;

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

    public static Member createMember(String email, String name, String passwordHash, MemberType memberType) {
        Member member = new Member();
        member.email = email;
        member.name = name;
        member.passwordHash = passwordHash;
        member.type = memberType;
        return member;
    }

    public boolean isAdmin() {
        return type.isAdmin();
    }

    // 관리자 권한을 가진 사람에 대한 커스텀 액션 실행 메소드 TODO throwable 로 만들기
    public Member ifAdmin(Consumer<Member> action) throws PermissionDeniedException {
        if (isAdmin()) {
            action.accept(this);
            return this;
        } else {
            return null;
        }
    }

    public <X extends Throwable> Member orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this != null) {
            return this;
        } else {
            throw (X) exceptionSupplier.get();
        }

    }

    public void checkEqualPassword(String passwordInput) {
        if (!HashUtil.equalPassword(passwordInput, passwordHash)) {
            throw LoginFailedException.wrongPassword();
        }
    }

    public void changePassword(String newPassword) {
        this.passwordHash = HashUtil.hashPassword(newPassword);
    }

    public boolean isNotEqualId(Long memberId) {
        return !this.id.equals(memberId);
    }
}
