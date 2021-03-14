package com.squadb.workassistantapi.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @DisplayName("책의 등록자는 관리자만 가능하다")
    @Test
    public void setRegistrantTest() {
        //given
        Member member = Member.createMember("admin@miridih.com", "1234", MemberType.NORMAL);
        Book book = new Book();

        //when then
        assertThrows(IllegalArgumentException.class, () -> book.setRegistrant(member));
    }

}