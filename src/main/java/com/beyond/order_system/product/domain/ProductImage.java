package com.beyond.order_system.product.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString(exclude = "product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer sortOrder;

    @Builder.Default
    private LocalDateTime createdTime = LocalDateTime.now();

    public void setProduct(Product product) {
        this.product = product;
    }
}
