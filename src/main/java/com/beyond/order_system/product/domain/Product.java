package com.beyond.order_system.product.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO member 엔티티와 관계성 설정 필요
    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double price;
    private String category;
    @Column(nullable = false)
    private Long stockQuantity;
    private String imagePath;
    @Builder.Default
    private LocalDateTime createdTime = LocalDateTime.now();
}
