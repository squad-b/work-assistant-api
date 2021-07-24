package com.squadb.workassistantapi.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.squadb.workassistantapi.domain.Member;
import com.squadb.workassistantapi.domain.Rental;
import com.squadb.workassistantapi.service.MemberService;
import com.squadb.workassistantapi.service.RentalService;
import com.squadb.workassistantapi.web.config.auth.CurrentLoginMember;
import com.squadb.workassistantapi.web.controller.dto.AuthResponseDto;
import com.squadb.workassistantapi.web.controller.dto.LoginMember;
import com.squadb.workassistantapi.web.controller.dto.LoginRequestDto;
import com.squadb.workassistantapi.web.controller.dto.MemberProfileResponseDto;
import com.squadb.workassistantapi.web.controller.dto.RentalRequestDto;
import com.squadb.workassistantapi.web.controller.dto.RentalResponseDto;
import com.squadb.workassistantapi.web.controller.dto.UpdateMemberRequestDto;
import com.squadb.workassistantapi.web.exception.LoginFailedException;
import com.squadb.workassistantapi.web.interceptor.CheckPermission;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final RentalService rentalService;

    @GetMapping("/auth")
    public ResponseEntity<AuthResponseDto> isLogin(@CurrentLoginMember LoginMember loginMember) {
        if (loginMember == null) { return ResponseEntity.ok(AuthResponseDto.fail("UNAUTHORIZED")); }
        return ResponseEntity.ok(AuthResponseDto.success(loginMember));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request, HttpSession session) {
        try {
            LoginMember loginMember = memberService.login(request.getEmail(), request.getPassword());
            session.setAttribute("LOGIN_MEMBER", loginMember);
            return ResponseEntity.ok(AuthResponseDto.success(loginMember));
        } catch (LoginFailedException e) {
            return ResponseEntity.ok(AuthResponseDto.fail(e.getResult()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession httpSession) {
        httpSession.invalidate();
        return ResponseEntity.ok("SUCCESS");
    }

    @CheckPermission
    @GetMapping("/members/{memberId}/profile")
    public ResponseEntity<MemberProfileResponseDto> getMemberProfile(@PathVariable Long memberId) {
        try {
            final Member member = memberService.findById(memberId);
            return new ResponseEntity<>(MemberProfileResponseDto.success(member), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(MemberProfileResponseDto.fail("NOT_FOUND"), HttpStatus.NOT_FOUND);
        }
    }

    @CheckPermission
    @GetMapping("/members/{memberId}/rentals")
    public ResponseEntity<List<RentalResponseDto>> getMemberBookRentals(@PathVariable Long memberId, RentalRequestDto rentalRequestDto) {
        final List<Rental> rentalList = rentalService.findMemberBookRentals(memberId, rentalRequestDto.getStatus());
        return new ResponseEntity<>(RentalResponseDto.of(rentalList), HttpStatus.OK);
    }

    @CheckPermission
    @PutMapping("/members/{memberId}")
    public ResponseEntity<String> updateMember(@RequestBody UpdateMemberRequestDto request,
                                               @PathVariable Long memberId) {
        try {
            memberService.updateMember(memberId, request.getPassword());
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.ok("FAIL");
        }
    }
}
