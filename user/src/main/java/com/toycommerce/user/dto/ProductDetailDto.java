package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "상품 상세 응답 DTO")
@Data
@Builder
public class ProductDetailDto {
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    
    @Schema(description = "상품명", example = "프리미엄 활전복")
    private String name;
    
    @Schema(description = "상품 설명", example = "완도산 최상급 활전복")
    private String description;
    
    @Schema(description = "SKU", example = "PRD-001")
    private String sku;
    
    @Schema(description = "가격", example = "30000")
    private Integer price;
    
    @Schema(description = "재고", example = "100")
    private Integer stock;
    
    @Schema(description = "상태", example = "ACTIVE")
    private String status;
    
    @Schema(description = "상품 템플릿 ID", example = "1")
    private Long productTemplateId;
    
    @Schema(description = "상품 템플릿명", example = "전복 세트")
    private String productTemplateName;
    
    @Schema(description = "상품 옵션 그룹 목록")
    private List<ProductOptionGroupDto> optionGroups;
    
    @Schema(description = "상품 이미지 목록")
    private List<AttachmentDto> images;
    
    @Schema(description = "대표 이미지 URL", example = "http://localhost:8082/uploads/2026/01/25/uuid.jpg")
    private String primaryImageUrl;
}

