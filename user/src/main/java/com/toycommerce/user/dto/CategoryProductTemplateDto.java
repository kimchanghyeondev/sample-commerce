package com.toycommerce.user.dto;

import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.product.ProductTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "카테고리-상품 템플릿 매핑 응답 DTO")
@Data
@Builder
public class CategoryProductTemplateDto {
    @Schema(description = "매핑 ID", example = "1")
    private Long mappingId;
    
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;
    
    // Category 정보
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;
    
    @Schema(description = "카테고리명", example = "수산물")
    private String categoryName;
    
    @Schema(description = "카테고리 설명", example = "신선한 수산물")
    private String categoryDescription;
    
    @Schema(description = "카테고리 표시 순서", example = "1")
    private Integer categoryDisplayOrder;
    
    @Schema(description = "카테고리 활성화 여부", example = "true")
    private Boolean categoryEnabled;
    
    // ProductTemplate 정보
    @Schema(description = "상품 템플릿 ID", example = "1")
    private Long productTemplateId;
    
    @Schema(description = "상품 템플릿명", example = "전복 세트")
    private String productTemplateName;
    
    @Schema(description = "상품 템플릿 설명", example = "다양한 전복 상품 세트")
    private String productTemplateDescription;
    
    @Schema(description = "상품 템플릿 상태", example = "ACTIVE")
    private String productTemplateStatus;
    
    @Schema(description = "대표 이미지 URL", example = "http://localhost:8082/uploads/2026/01/25/uuid.jpg")
    private String primaryImageUrl;

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

    public static CategoryProductTemplateDto from(Category category, ProductTemplate productTemplate, Long mappingId, Integer sortOrder, String primaryImageUrl) {
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
                .primaryImageUrl(primaryImageUrl)
                .build();
    }
}

