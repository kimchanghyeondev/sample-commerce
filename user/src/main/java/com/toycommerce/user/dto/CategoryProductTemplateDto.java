package com.toycommerce.user.dto;

import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.product.ProductTemplate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryProductTemplateDto {
    private Long mappingId;
    private Integer sortOrder;
    
    // Category 정보
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private Integer categoryDisplayOrder;
    private Boolean categoryEnabled;
    
    // ProductTemplate 정보
    private Long productTemplateId;
    private String productTemplateName;
    private String productTemplateDescription;
    private String productTemplateStatus;

    public static CategoryProductTemplateDto from(Category category, ProductTemplate productTemplate, Long mappingId, Integer sortOrder) {
        return CategoryProductTemplateDto.builder()
                .mappingId(mappingId)
                .sortOrder(sortOrder)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .categoryDescription(category.getDescription())
                .categoryDisplayOrder(category.getDisplayOrder())
                .categoryEnabled(category.getEnabled())
                .productTemplateId(productTemplate.getId())
                .productTemplateName(productTemplate.getName())
                .productTemplateDescription(productTemplate.getDescription())
                .productTemplateStatus(productTemplate.getStatus() != null ? productTemplate.getStatus().name() : null)
                .build();
    }
}

