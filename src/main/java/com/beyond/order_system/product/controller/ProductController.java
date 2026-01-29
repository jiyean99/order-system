package com.beyond.order_system.product.controller;

import com.beyond.order_system.product.dto.request.ProductCreateReqDto;
import com.beyond.order_system.product.dto.response.ProductDetailResDto;
import com.beyond.order_system.product.dto.response.ProductListResDto;
import com.beyond.order_system.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import java.util.List;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(
            @ModelAttribute @Valid ProductCreateReqDto dto,
            @RequestParam(value = "productImages", required = false) List<MultipartFile> productImages,
            @AuthenticationPrincipal String principal
    ) {
        ProductDetailResDto res = productService.create(dto, productImages, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 상품 상세 조회 (공개)
    @GetMapping("/detail/{id}")
    public ProductDetailResDto findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    // 상품 목록 조회 (공개, 페이징/정렬)
    @GetMapping("/list")
    public ProductListResDto findAll(
            @PageableDefault(size = 10, sort = "createdTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return productService.findAll(pageable);
    }
}
