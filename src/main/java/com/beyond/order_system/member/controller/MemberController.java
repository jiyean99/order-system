package com.beyond.order_system.member.controller;

import com.beyond.order_system.member.dto.request.MemberCreateReqDto;
import com.beyond.order_system.member.dto.request.MemberLoginReqDto;
import com.beyond.order_system.member.dto.response.MemberDetailResDto;
import com.beyond.order_system.member.dto.response.MemberListResDto;
import com.beyond.order_system.member.dto.response.MyInfoResDto;
import com.beyond.order_system.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {
    /* *********************** DI주입 *********************** */
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /* *********************** 컨트롤러 *********************** */
    // 회원가입
    @PostMapping("/create")
    public void create(@RequestBody @Valid MemberCreateReqDto dto) {
        memberService.save(dto);
    }


    // 로그인
    @PostMapping("/doLogin")
    public void login(@RequestBody @Valid MemberLoginReqDto dto) {

    }

    // 회원 목록 조회
    @GetMapping("/list")
    public MemberListResDto findAll() {
        return null;
    }

    // 내 정보 조회
    @GetMapping("/myinfo")
    // TODO AuthenticationPrincipal 처리 필요
    public MyInfoResDto findByMe(String principal) {
        return null;
    }

    // 회원 상세 조회
    @GetMapping("/detail/{id}")
    public MemberDetailResDto findById() {
        return null;
    }
}
