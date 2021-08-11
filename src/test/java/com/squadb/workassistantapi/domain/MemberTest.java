package com.squadb.workassistantapi.domain;

import static com.squadb.workassistantapi.domain.Member.createMember;
import static com.squadb.workassistantapi.domain.MemberType.ADMIN;
import static com.squadb.workassistantapi.domain.MemberType.NORMAL;

class MemberTest {
    static final Member 관리자 = createMember("admin@miridih.com", "관리자", "1234", ADMIN);
    static final Member 일반회원 = createMember("normal@miridih.com", "김일반", "1234", NORMAL);
}
