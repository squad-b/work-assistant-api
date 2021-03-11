package com.squadb.workassistantapi.repository;

import com.squadb.workassistantapi.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
