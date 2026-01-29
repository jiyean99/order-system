package com.beyond.order_system.ordering.controller;

import com.beyond.order_system.ordering.dto.request.OrderCreateReqDto;
import com.beyond.order_system.ordering.dto.response.MyOrdersResDto;
import com.beyond.order_system.ordering.dto.response.OrderListResDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ordering")
public class OrderingController {
    // 주문하기
    @PostMapping("/create")
    public void create (OrderCreateReqDto dto){

    }

    // 주문 목록 조회
    @GetMapping("/list/response")
    public OrderListResDto findAll(){
        return null;
    }

    // 내 주문 목록 조회
    @GetMapping("/myorders")
    public MyOrdersResDto findByMe(){
        return null;
    }
}
