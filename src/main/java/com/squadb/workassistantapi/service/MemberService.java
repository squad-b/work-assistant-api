package com.squadb.workassistantapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.MemberRepository;
import com.squadb.workassistantapi.web.exception.LoginFailedException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member findById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Member ID:[%d]", memberId)));
    }

    @Transactional(readOnly = true)
    public Member login(String email, String password) {
        final Member findMember = memberRepository.findByEmail(email).orElseThrow(LoginFailedException::noSuchMember);
        findMember.checkEqualPassword(password);
        return findMember;
    }

    @Transactional
    public void updateMember(Long memberId, String newPassword) {
        final Member member = memberRepository.findById(memberId).orElseThrow();
        member.changePassword(newPassword);
    }
}
