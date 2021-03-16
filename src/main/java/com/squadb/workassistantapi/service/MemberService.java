package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.MemberRepository;
import com.squadb.workassistantapi.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member findById(final long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Member ID:[%d]", memberId)));
    }

    public long login(String email, String password) {
        final Member findMember = memberRepository.findByEmail(email);
        if (findMember == null) {
            throw new NoSuchElementException("존재하지 않는 회원입니다.");
        }
        if (!HashUtil.validatePassword(password, findMember.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return findMember.getId();
    }
}
