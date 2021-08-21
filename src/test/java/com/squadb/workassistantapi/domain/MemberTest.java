package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.web.exception.LoginFailedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.squadb.workassistantapi.domain.Member.createMember;
import static com.squadb.workassistantapi.domain.MemberType.ADMIN;
import static com.squadb.workassistantapi.domain.MemberType.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberTest {
    public static final Member 관리자 = createMember("admin@miridih.com", "관리자", "1234", ADMIN);
    public static final Member 일반회원 = createMember("normal@miridih.com", "김일반", "1234", NORMAL);

    @DisplayName("회원은 비밀번호를 검사할 수 있다.")
    @Test
    void checkEqualPasswordTest() {
        // given
        PasswordEncryptor wrongPasswordEncryptor = new PasswordEncryptor() {
            @Override
            public String encrypt(String plain) {
                return null;
            }

            @Override
            public boolean match(String password, String plain) {
                return false;
            }
        };

        // when //then
        assertThatThrownBy(() -> 일반회원.checkEqualPassword("wrong password", wrongPasswordEncryptor))
                .isInstanceOf(LoginFailedException.class);
    }

    @DisplayName("회원은 비밀번호를 바꿀 수 있다.")
    @Test
    void changePasswordTest() {
        // given
        PasswordEncryptor noEncrypt = new PasswordEncryptor() {
            @Override
            public String encrypt(String plain) {
                return plain;
            }

            @Override
            public boolean match(String password, String plain) {
                return false;
            }
        };
        Member 유인근 = Member.createMember("ayden@miridih.com", "유인근", "1234", NORMAL);

        // when
        유인근.changePassword("5678", noEncrypt);

        // then
        assertThat(유인근.getPasswordHash()).isEqualTo("5678");
    }

}
