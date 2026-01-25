package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "상품 템플릿 응답 DTO")
@Data
@Builder
public class ProductTemplateDto {
    @Schema(description = "상품 템플릿 ID", example = "1")
    private Long id;
    
    @Schema(description = "상품 템플릿명", example = "전복 세트")
    private String name;
    
    @Schema(description = "상품 템플릿 설명", example = "다양한 전복 상품 세트")
    private String description;
    
    @Schema(description = "상태", example = "ACTIVE")
    private String status;
    
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;
    
    @Schema(description = "매핑 ID", example = "1")
    private Long mappingId;
    
    @Schema(description = "대표 이미지 URL", example = "http://localhost:8082/uploads/2026/01/25/uuid.jpg")
    private String primaryImageUrl;
}

