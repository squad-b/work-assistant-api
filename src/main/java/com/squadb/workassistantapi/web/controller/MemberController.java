package com.squadb.workassistantapi.web.controller;

import com.squadb.workassistantapi.service.MemberService;
import com.squadb.workassistantapi.web.controller.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
