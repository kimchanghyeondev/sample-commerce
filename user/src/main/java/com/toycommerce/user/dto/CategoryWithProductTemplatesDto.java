package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "카테고리별 상품 템플릿 그룹 응답 DTO")
@Data
@Builder
public class CategoryWithProductTemplatesDto {
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
    
    @Schema(description = "상품 템플릿 목록")
    private List<ProductTemplateDto> productTemplates;
}

