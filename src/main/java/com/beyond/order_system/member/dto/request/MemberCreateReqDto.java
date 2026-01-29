package com.beyond.order_system.member.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberCreateReqDto {
    private String name;
    private String email;
    private String password;
}
