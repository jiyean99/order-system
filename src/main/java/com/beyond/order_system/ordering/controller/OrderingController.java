package com.beyond.order_system.ordering.controller;

import com.beyond.order_system.ordering.dto.request.OrderCreateReqDto;
import com.beyond.order_system.ordering.dto.response.MyOrdersResDto;
import com.beyond.order_system.ordering.dto.response.OrderListResDto;
import com.beyond.order_system.ordering.service.OrderingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
    /* *********************** DI주입 *********************** */
    private final OrderingService orderingService;

    @Autowired
    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    /* *********************** 컨트롤러 *********************** */
    // 주문하기
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid OrderCreateReqDto dto,
                                    @AuthenticationPrincipal String principal) {
        orderingService.create(dto, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }

    // 주문 목록 조회
    @GetMapping("/list/response")
    public OrderListResDto findAll() {
        return null;
    }

    // 내 주문 목록 조회
    @GetMapping("/myorders")
    public MyOrdersResDto findByMe() {
        return null;
    }
}
