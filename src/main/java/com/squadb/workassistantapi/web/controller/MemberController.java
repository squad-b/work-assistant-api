package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.service.MemberService;
import com.squadb.workassistantapi.web.controller.dto.LoginRequestDto;
import com.squadb.workassistantapi.web.controller.dto.MemberProfileResponseDto;
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
    public ResponseEntity<String> isLogin(HttpSession session) {
        if (session.getAttribute("MEMBER_ID") == null) {
            return ResponseEntity.ok("UNAUTHORIZED");
        }
        return ResponseEntity.ok("AUTHORIZED");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request, HttpSession session) {
        try {
            long id = memberService.login(request.getEmail(), request.getPassword());
            session.setAttribute("MEMBER_ID", id);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.ok("FAIL");
        }
    }

    @CheckPermission
    @GetMapping("/members/{memberId}/profile")
    public ResponseEntity<MemberProfileResponseDto> getMyProfile(@PathVariable long memberId) {
        try {
            final Member member = memberService.findById(memberId);
            return new ResponseEntity<>(MemberProfileResponseDto.success(member), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(MemberProfileResponseDto.fail("NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
    }

}
