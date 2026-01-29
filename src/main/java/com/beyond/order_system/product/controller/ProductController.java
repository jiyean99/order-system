package com.beyond.order_system.product.controller;

import com.beyond.order_system.product.dto.request.ProductCreateReqDto;
import com.beyond.order_system.product.dto.response.ProductDetailResDto;
import com.beyond.order_system.product.dto.response.ProductListResDto;
import com.beyond.order_system.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/product")
public class ProductController {
    /* *********************** DI 주입 *********************** */
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /* *********************** 컨트롤러 *********************** */
    // 상품 등록
    @PostMapping("/create")
    public void createPost(@ModelAttribute ProductCreateReqDto dto,
                           @RequestParam(value = "productImage") MultipartFile productImage) {

    }

    // 상품 상세 조회
    @GetMapping("/detail/{id}")
    public ProductDetailResDto findById() {
        return null;
    }

    // 상품 목록 조회
    @GetMapping("/list")
    public ProductListResDto findAll() {
        return null;
    }
}
