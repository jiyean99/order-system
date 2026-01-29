package com.beyond.order_system.member.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.member.dto.request.MemberCreateReqDto;
import com.beyond.order_system.member.dto.request.MemberLoginReqDto;
import com.beyond.order_system.member.dto.response.MemberDetailResDto;
import com.beyond.order_system.member.dto.response.MemberListResDto;
import com.beyond.order_system.member.dto.response.MyInfoResDto;
import com.beyond.order_system.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class MemberService {
    /* *********************** DI주입 *********************** */
    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /* *********************** 서비스 *********************** */
    public void save(MemberCreateReqDto dto) {
        memberRepository.save(dto.toEntity());
    }

    public void login(String principal) {

    }

    public MemberListResDto findAll() {
        return null;
    }

    public MyInfoResDto findByMe() {
        return null;
    }

    public MemberDetailResDto findById() {
        return null;
    }
}
