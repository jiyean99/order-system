package com.beyond.order_system.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RabbitMqStockDto {
    private Long productId;
    private Long productCount;
}
