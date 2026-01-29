package com.beyond.order_system.product.service;

import com.beyond.order_system.common.storage.S3Service;
import com.beyond.order_system.common.storage.S3UploadResult;
import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.domain.ProductImage;
import com.beyond.order_system.product.dto.request.ProductCreateReqDto;
import com.beyond.order_system.product.dto.response.ProductDetailResDto;
import com.beyond.order_system.product.dto.response.ProductListResDto;
import com.beyond.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final EntityManager em;

    @Autowired
    public ProductService(ProductRepository productRepository, S3Service s3Service, EntityManager em) {
        this.productRepository = productRepository;
        this.s3Service = s3Service;
        this.em = em;
    }

    public ProductDetailResDto create(ProductCreateReqDto dto, List<MultipartFile> productImages, String principal) {

        Long memberId = Long.valueOf(principal);

        Product product = Product.builder()
                .member(em.getReference(Member.class, memberId))
                .name(dto.getName())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .stockQuantity(dto.getStockQuantity())
                .build();

        Product saved = productRepository.save(product);

        if (productImages == null || productImages.isEmpty()) {
            return ProductDetailResDto.fromEntity(saved);
        }

        int order = 0;
        for (MultipartFile file : productImages) {
            if (file == null || file.isEmpty()) continue;

            S3UploadResult uploaded = s3Service.upload(file, "products/" + saved.getId());

            saved.addImage(ProductImage.builder()
                    .s3Key(uploaded.getKey())
                    .url(uploaded.getUrl())
                    .sortOrder(order++)
                    .build());
        }

        Product savedWithImages = productRepository.save(saved);

        return ProductDetailResDto.fromEntity(savedWithImages);
    }

    @Transactional(readOnly = true)
    public ProductDetailResDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        return ProductDetailResDto.fromEntity(product);
    }

    @Transactional(readOnly = true)
    public ProductListResDto findAll(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);

        return ProductListResDto.builder()
                .content(page.getContent().stream()
                        .map(ProductListResDto.ProductListItem::fromEntity)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

}
