package com.toycommerce.admin.dto.product;

import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "상품 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long id;
    
    @Schema(description = "상품 템플릿 ID", example = "1")
    private Long productTemplateId;
    
    @Schema(description = "상품 템플릿명", example = "전복 세트")
    private String productTemplateName;
    
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
    private EntityStatus status;
    
    @Schema(description = "옵션 그룹 목록")
    private List<OptionGroupResponse> optionGroups;
    
    @Schema(description = "이미지 목록")
    private List<AttachmentResponse> images;
    
    @Schema(description = "대표 이미지 URL", example = "http://localhost:8082/uploads/2026/01/26/uuid.jpg")
    private String primaryImageUrl;
    
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionGroupResponse {
        private Long id;
        private String name;
        private EntityStatus status;
        private List<OptionResponse> options;

        public static OptionGroupResponse from(ProductOptionGroup group, List<ProductOption> options) {
            List<OptionResponse> optionResponses = options != null
                    ? options.stream()
                        .map(OptionResponse::from)
                        .toList()
                    : List.of();

            return OptionGroupResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .status(group.getStatus())
                    .options(optionResponses)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionResponse {
        private Long id;
        private String name;
        private EntityStatus status;

        public static OptionResponse from(ProductOption option) {
            return OptionResponse.builder()
                    .id(option.getId())
                    .name(option.getName())
                    .status(option.getStatus())
                    .build();
        }
    }

    public static ProductResponse from(Product product, List<OptionGroupResponse> optionGroups) {
        return ProductResponse.builder()
                .id(product.getId())
                .productTemplateId(product.getProductTemplate().getId())
                .productTemplateName(product.getProductTemplate().getName())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .optionGroups(optionGroups)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductResponse from(Product product, List<OptionGroupResponse> optionGroups, List<AttachmentResponse> images) {
        // 대표 이미지 URL 설정: isPrimary가 true인 것 우선, 없으면 첫 번째 이미지
        String primaryImageUrl = null;
        if (images != null && !images.isEmpty()) {
            primaryImageUrl = images.stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .findFirst()
                    .map(AttachmentResponse::getFileUrl)
                    .orElse(images.get(0).getFileUrl());
        }
        
        return ProductResponse.builder()
                .id(product.getId())
                .productTemplateId(product.getProductTemplate().getId())
                .productTemplateName(product.getProductTemplate().getName())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .optionGroups(optionGroups)
                .images(images)
                .primaryImageUrl(primaryImageUrl)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductResponse simpleFrom(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productTemplateId(product.getProductTemplate().getId())
                .productTemplateName(product.getProductTemplate().getName())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductResponse simpleFrom(Product product, String primaryImageUrl) {
        return ProductResponse.builder()
                .id(product.getId())
                .productTemplateId(product.getProductTemplate().getId())
                .productTemplateName(product.getProductTemplate().getName())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .primaryImageUrl(primaryImageUrl)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

