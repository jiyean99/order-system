package com.beyond.order_system.member.controller;

import com.beyond.order_system.member.dto.request.MemberCreateReqDto;
import com.beyond.order_system.member.dto.request.MemberLoginReqDto;
import com.beyond.order_system.member.dto.responce.MemberListResDto;
import com.beyond.order_system.member.dto.responce.MyInfoResDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {
    // 회원가입
    @PostMapping("/create")
    public void create(MemberCreateReqDto dto) {

    }

    // user 로그인
    @PostMapping("/doLogin")
    public void userLogin(MemberLoginReqDto dto){

    }

    // admin 로그인
    @PostMapping("/doLogin")
    public void adminLogin(MemberLoginReqDto dto){

    }

    // 회원 목록 조회
    @GetMapping("/list")
    public MemberListResDto memberList(){
        return null;
    }

    // 내 정보 조회
    @GetMapping("/myinfo")
    public MyInfoResDto myInfo(){
        return null;
    }

    // 회원 상세 조회
    @GetMapping("/detail/{id}")
    public MemberDetailResDto memberDetail(){
        return null;
    }
}
