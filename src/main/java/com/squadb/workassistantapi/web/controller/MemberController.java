package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.service.MemberService;
import com.squadb.workassistantapi.web.controller.dto.LoginRequestDto;
import com.squadb.workassistantapi.web.controller.dto.LoginResponseDto;
import com.squadb.workassistantapi.web.controller.dto.MemberProfileResponseDto;
import com.squadb.workassistantapi.web.controller.dto.UpdateMemberRequestDto;
import com.squadb.workassistantapi.web.exception.LoginFailedException;
import com.squadb.workassistantapi.web.interceptor.CheckPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/auth")
    public ResponseEntity<LoginResponseDto> isLogin(HttpSession session) {
        if (session.getAttribute(Member.LOGIN_SESSION_KEY) == null) {
            return ResponseEntity.ok(LoginResponseDto.fail("UNAUTHORIZED"));
        }
        return ResponseEntity.ok(LoginResponseDto.success((Long) session.getAttribute(Member.LOGIN_SESSION_KEY)));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request, HttpSession session) {
        try {
            long memberId = memberService.login(request.getEmail(), request.getPassword());
            session.setAttribute(Member.LOGIN_SESSION_KEY, memberId);
            return ResponseEntity.ok(LoginResponseDto.success(memberId));
        } catch (LoginFailedException e) {
            return ResponseEntity.ok(LoginResponseDto.fail(e.getResult()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession httpSession) {
        httpSession.invalidate();
        return ResponseEntity.ok("SUCCESS");
    }

    @CheckPermission
    @GetMapping("/members/{memberId}/profile")
    public ResponseEntity<MemberProfileResponseDto> getMemberProfile(@PathVariable long memberId) {
        try {
            final Member member = memberService.findById(memberId);
            return new ResponseEntity<>(MemberProfileResponseDto.success(member), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(MemberProfileResponseDto.fail("NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
    }

    @CheckPermission
    @PutMapping("/members/{memberId}")
    public ResponseEntity<String> updateMember(@RequestBody UpdateMemberRequestDto request,
                                               @PathVariable long memberId) {
        try {
            memberService.updateMember(memberId, request.getPassword());
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.ok("FAIL");
        }
    }

}
