package com.squadb.workassistantapi.service;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.repository.MemberRepository;
import com.squadb.workassistantapi.util.HashUtil;
import com.squadb.workassistantapi.web.exception.LoginFailedException;
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

    public long login(String email, String password) {
        final Member findMember = memberRepository.findByEmail(email);
        if (findMember == null) {
            throw LoginFailedException.noSuchMember();
        }
        if (!HashUtil.validatePassword(password, findMember.getPasswordHash())) {
            throw LoginFailedException.wrongPassword();
        }
        return findMember.getId();
    }

    @Transactional
    public void updatePassword(long loginMemberId, String oldPassword, String newPassword) {
        final Member findMember = memberRepository.findById(loginMemberId).orElseThrow();
        findMember.validatePassword(oldPassword);
        findMember.updatePassword(newPassword);
    }
}
