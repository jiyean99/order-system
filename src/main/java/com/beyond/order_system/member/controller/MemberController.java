package com.beyond.order_system.member.controller;

import com.beyond.order_system.common.auth.JwtTokenProvider;
import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.member.dto.request.MemberCreateReqDto;
import com.beyond.order_system.member.dto.request.MemberLoginReqDto;
import com.beyond.order_system.member.dto.response.MemberDetailResDto;
import com.beyond.order_system.member.dto.response.MemberListResDto;
import com.beyond.order_system.member.dto.response.MyInfoResDto;
import com.beyond.order_system.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {
    /* *********************** DI주입 *********************** */
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /* *********************** 컨트롤러 *********************** */
    // 회원가입
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid MemberCreateReqDto dto) {
        memberService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }

    // 로그인
    @PostMapping("/doLogin")
    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginReqDto dto) {
        Member member = memberService.login(dto);
        String token = jwtTokenProvider.createToken(member);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    // 회원 목록 조회
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public MemberListResDto findAll() {
        return memberService.findAll();
    }

    // 내 정보 조회
    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo(@AuthenticationPrincipal String principal) {
        MyInfoResDto dto = memberService.myInfo(principal); // principal = "memberId"
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // 회원 상세 조회
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MemberDetailResDto findById(@PathVariable Long id) {
        return memberService.findById(id);
    }
}
