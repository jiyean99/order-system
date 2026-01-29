package com.beyond.order_system.ordering.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ordering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO member 엔티티 관계성 설정 필요
    @Column(nullable = false)
    private Long memberId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Builder.Default
    private LocalDateTime createdTime = LocalDateTime.now();
}
