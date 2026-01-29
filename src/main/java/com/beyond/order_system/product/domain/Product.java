package com.beyond.order_system.product.domain;

import com.beyond.order_system.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = "images")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private String category;

    @Column(nullable = false)
    private Long stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdTime = LocalDateTime.now();

    public void addImage(ProductImage image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void decreaseStock(long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("감소 수량은 1 이상이어야 합니다.");
        }

        long rest = this.stockQuantity - quantity;
        if (rest < 0) {
            throw new IllegalArgumentException("재고 부족");
        }

        this.stockQuantity = rest;
    }

}
