package com.beyond.order_system.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDetailResDto {
    private Long id;
    private String name;
    private String category;
    private Double price;
    private Long stockQuantity;
    private String imagePath;
}
