package com.toycommerce.gateway.dto;

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
    private List<ProductOptionGroupDto> optionGroups;
    private List<CategoryInfoDto> categories;
    
    @Data
    @Builder
    public static class CategoryInfoDto {
        private Long categoryId;
        private String categoryName;
    }
}

