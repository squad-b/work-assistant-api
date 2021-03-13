package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member findById(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Member ID:[%d]", memberId)));
    }
}
