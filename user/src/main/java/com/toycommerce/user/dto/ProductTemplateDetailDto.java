package com.toycommerce.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductTemplateDetailDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private List<ProductInfoDto> products;
    private List<CategoryInfoDto> categories;
    
    @Data
    @Builder
    public static class ProductInfoDto {
        private Long productId;
        private String productName;
        private String productDescription;
        private String sku;
        private Integer price;
        private Integer stock;
        private String status;
    }
    
    @Data
    @Builder
    public static class CategoryInfoDto {
        private Long categoryId;
        private String categoryName;
    }
}

