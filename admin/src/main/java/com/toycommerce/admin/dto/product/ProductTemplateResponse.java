package com.toycommerce.admin.dto.product;

import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.ProductTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTemplateResponse {
    private Long id;
    private String name;
    private String description;
    private EntityStatus status;
    private List<CategoryInfo> categories;
    private Integer productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    public static ProductTemplateResponse from(ProductTemplate template) {
        List<CategoryInfo> categoryInfos = template.getCategories() != null
                ? template.getCategories().stream()
                    .map(mapping -> CategoryInfo.builder()
                            .id(mapping.getCategory().getId())
                            .name(mapping.getCategory().getName())
                            .build())
                    .toList()
                : List.of();

        int productCount = template.getProducts() != null ? template.getProducts().size() : 0;

        return ProductTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .status(template.getStatus())
                .categories(categoryInfos)
                .productCount(productCount)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}

