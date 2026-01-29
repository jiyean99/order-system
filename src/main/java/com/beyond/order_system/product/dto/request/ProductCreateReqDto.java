package com.beyond.order_system.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductCreateReqDto {
    private String name;
    private Double price;
    private String category;
    private Long stockQuantity;
}
