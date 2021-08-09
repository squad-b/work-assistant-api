package com.squadb.workassistantapi.domain;

import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {

    @DisplayName("책의 등록자는 관리자만 가능하다")
    @Test
    public void createBookNormalMemberTest() {
        //given
        Member member = Member.createMember("admin@miridih.com", "피플팀", "1234", MemberType.NORMAL);

        //when then
        assertThrows(NoAuthorizationException.class, () -> Book.builder().registrant(member).build());
    }

}