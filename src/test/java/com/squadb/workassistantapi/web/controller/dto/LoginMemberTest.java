package com.squadb.workassistantapi.web.controller.dto;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.MemberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;

class LoginMemberTest {

    @Test
    @DisplayName("session 에 저장한 LoginMember 와 session 에서 가져온 LoginMember 는 값이 같아야 한다.")
    public void saveTest() {
        // given
        Member member = Member.createMember("admin@miridih.com", "피플팀", "1234", MemberType.NORMAL);
        MockHttpSession mockHttpSession = new MockHttpSession();

        // when
        LoginMember loginMember = LoginMember.putInSession(member, mockHttpSession);

        // then
        LoginMember loginMemberFromSession = LoginMember.getFromSession(mockHttpSession);
        assertThat(loginMemberFromSession).isEqualTo(loginMember);
    }
}