package com.beyond.order_system.product.dto.response;

import com.beyond.order_system.product.domain.Product;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductListResDto {

    private List<ProductListItem> content;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ProductListItem {
        private Long id;
        private String name;
        private String category;
        private Double price;
        private Long stockQuantity;

        // 대표 이미지 1장 (없으면 null)
        private String thumbnailUrl;

        public static ProductListItem fromEntity(Product product) {
            String thumb = product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl();
            return ProductListItem.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .stockQuantity(product.getStockQuantity())
                    .thumbnailUrl(thumb)
                    .build();
        }
    }
}
