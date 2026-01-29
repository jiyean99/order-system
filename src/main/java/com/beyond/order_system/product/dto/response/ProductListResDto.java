package com.beyond.order_system.product.dto.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductListResDto {

    private List<ProductListItem> content;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ProductListItem {
        private Long id;
        private String name;
        private String category;
        private Integer price;
        private Integer stockQuantity;
        private String imagePath;
    }
}
