package com.squadb.workassistantapi.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.squadb.workassistantapi.domain.exceptions.NoAuthorizationException;

class BookTest {

    @DisplayName("책의 등록자는 관리자만 가능하다")
    @Test
    public void setRegistrantTest() {
        //given
        Member member = Member.createMember("admin@miridih.com", "피플팀", "1234", MemberType.NORMAL);
        Book book = new Book();

        //when then
        assertThrows(NoAuthorizationException.class, () -> book.setRegistrant(member));
    }

    @DisplayName("일반 멤버는 책을 삭제할 수 없다.")
    @Test
    public void deletePermissionTest() {
        // given
        Member normalMember = Member.createMember("admin@miridih.com", "피플팀", "1234", MemberType.NORMAL);
        Book book = new Book();

        // when //then
        assertThrows(NoAuthorizationException.class, () -> book.checkDeletePermission(normalMember));
    }

}