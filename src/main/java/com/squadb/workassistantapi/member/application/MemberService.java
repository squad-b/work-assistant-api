package com.squadb.workassistantapi.member.application;

import com.squadb.workassistantapi.member.domain.LoginFailedException;
import com.squadb.workassistantapi.member.domain.Member;
import com.squadb.workassistantapi.member.domain.MemberRepository;
import com.squadb.workassistantapi.member.domain.PasswordEncryptor;
import com.squadb.workassistantapi.member.dto.LoginMember;
import com.squadb.workassistantapi.member.dto.MemberRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.squadb.workassistantapi.member.domain.Member.createMember;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional(readOnly = true)
    public Member findById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Member ID:[%d]", memberId)));
    }

    @Transactional(readOnly = true)
    public LoginMember login(String email, String plainPassword) {
        final Member findMember = memberRepository.findByEmail(email).orElseThrow(LoginFailedException::noSuchMember);
        findMember.checkEqualPassword(plainPassword, passwordEncryptor);
        return new LoginMember(findMember.getId(), findMember.getType());
    }

    @Transactional
    public Long create(MemberRequestDto memberRequestDto) {
        Member persistMember = memberRepository.save(createMember(memberRequestDto.getEmail(), memberRequestDto.getName(), memberRequestDto.getPassword(), passwordEncryptor, memberRequestDto.getMemberType()));
        return persistMember.getId();
    }

    @Transactional
    public void updateMember(Long memberId, String newPassword) {
        final Member member = memberRepository.findById(memberId).orElseThrow();
        member.changePassword(newPassword, passwordEncryptor);
    }
}
