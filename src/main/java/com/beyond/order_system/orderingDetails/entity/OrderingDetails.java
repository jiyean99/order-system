package com.beyond.order_system.orderingDetails.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO Order 엔티티와 관계성 설정 필요
    private Long orderId;
    // TODO Product 엔티티와 관계성 설정 필요
    private Long productId;
    @Column(nullable = false)
    private Long quantity;
    @Builder.Default
    private LocalDateTime createdTime = LocalDateTime.now();
}
