package com.beyond.order_system.product.controller;

import com.beyond.order_system.product.dto.request.ProductCreateReqDto;
import com.beyond.order_system.product.dto.response.ProductDetailResDto;
import com.beyond.order_system.product.dto.response.ProductListResDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {
    // 상품 등록
    @PostMapping("/create")
    public void createPost(ProductCreateReqDto dto){

    }
    // 상품 상세 조회
    @GetMapping("/detail/{id}")
    public ProductDetailResDto findById(){
        return null;
    }

    // 상품 목록 조회
    @GetMapping("/list")
    public ProductListResDto findAll(){
        return null;
    }
}
