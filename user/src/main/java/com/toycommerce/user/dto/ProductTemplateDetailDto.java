package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "상품 템플릿 상세 응답 DTO")
@Data
@Builder
public class ProductTemplateDetailDto {
    @Schema(description = "상품 템플릿 ID", example = "1")
    private Long id;
    
    @Schema(description = "상품 템플릿명", example = "전복 세트")
    private String name;
    
    @Schema(description = "상품 템플릿 설명", example = "다양한 전복 상품 세트")
    private String description;
    
    @Schema(description = "상태", example = "ACTIVE")
    private String status;
    
    @Schema(description = "상품 목록")
    private List<ProductInfoDto> products;
    
    @Schema(description = "카테고리 목록")
    private List<CategoryInfoDto> categories;
    
    @Schema(description = "템플릿 이미지 목록")
    private List<AttachmentDto> images;
    
    @Schema(description = "대표 이미지 URL", example = "http://localhost:8082/uploads/2026/01/25/uuid.jpg")
    private String primaryImageUrl;
    
    @Schema(description = "상품 정보 DTO")
    @Data
    @Builder
    public static class ProductInfoDto {
        @Schema(description = "상품 ID", example = "1")
        private Long productId;
        
        @Schema(description = "상품명", example = "프리미엄 활전복")
        private String productName;
        
        @Schema(description = "상품 설명", example = "완도산 최상급 활전복")
        private String productDescription;
        
        @Schema(description = "SKU", example = "PRD-001")
        private String sku;
        
        @Schema(description = "가격", example = "30000")
        private Integer price;
        
        @Schema(description = "재고", example = "100")
        private Integer stock;
        
        @Schema(description = "상태", example = "ACTIVE")
        private String status;
        
        @Schema(description = "대표 이미지 URL", example = "http://localhost:8082/uploads/2026/01/25/uuid.jpg")
        private String primaryImageUrl;
    }
    
    @Schema(description = "카테고리 정보 DTO")
    @Data
    @Builder
    public static class CategoryInfoDto {
        @Schema(description = "카테고리 ID", example = "1")
        private Long categoryId;
        
        @Schema(description = "카테고리명", example = "수산물")
        private String categoryName;
    }
}

