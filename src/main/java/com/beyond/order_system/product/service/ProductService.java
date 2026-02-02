package com.beyond.order_system.product.service;

import com.beyond.order_system.common.storage.S3Service;
import com.beyond.order_system.common.storage.S3UploadResult;
import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.dto.request.ProductCreateReqDto;
import com.beyond.order_system.product.dto.request.ProductSearchReqDto;
import com.beyond.order_system.product.dto.response.ProductDetailResDto;
import com.beyond.order_system.product.dto.response.ProductListResDto;
import com.beyond.order_system.product.dto.response.ProductResDto;
import com.beyond.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    public ProductDetailResDto create(ProductCreateReqDto dto, MultipartFile productImage, String principal) {
        Long memberId = Long.valueOf(principal);

        Product product = Product.builder()
                .member(em.getReference(Member.class, memberId))
                .name(dto.getName())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .stockQuantity(dto.getStockQuantity())
                .build();

        Product saved = productRepository.save(product);

        if (productImage != null && !productImage.isEmpty()) {
            S3UploadResult uploaded = s3Service.upload(productImage, "products/" + saved.getId());
            saved.updateImagePath(uploaded.getUrl());
        }

        return ProductDetailResDto.fromEntity(saved);
    }


    @Transactional(readOnly = true)
    public ProductDetailResDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        return ProductDetailResDto.fromEntity(product);
    }

    //    @Transactional(readOnly = true)
//    public ProductListResDto findAll(Pageable pageable) {
//        Page<Product> page = productRepository.findAll(pageable);
//
//        return ProductListResDto.builder()
//                .content(page.getContent().stream()
//                        .map(ProductListResDto.ProductListItem::fromEntity)
//                        .toList())
//                .page(page.getNumber())
//                .size(page.getSize())
//                .totalElements(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .build();
//    }
    @Transactional(readOnly = true)
    public Page<ProductResDto> findAll(Pageable pageable, ProductSearchReqDto searchDto) {
        Specification<Product> specification = new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                if (searchDto.getProductName() != null) {
                    predicateList.add(criteriaBuilder.like(root.get("name"), "%" + searchDto.getProductName() + "%"));
                }
                if (searchDto.getCategory() != null) {
                    predicateList.add(criteriaBuilder.equal(root.get("category"), searchDto.getCategory()));
                }
                Predicate[] predicateArr = new Predicate[predicateList.size()];
                for (int i = 0; i < predicateArr.length; i++) {
                    predicateArr[i] = predicateList.get(i);
                }
                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };
        Page<Product> postList = productRepository.findAll(specification, pageable);
        return postList.map(p -> ProductResDto.fromEntity(p));
    }

}
