package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "상품 옵션 응답 DTO")
@Data
@Builder
public class ProductOptionDto {
    @Schema(description = "옵션 ID", example = "1")
    private Long id;
    
    @Schema(description = "옵션명", example = "1kg")
    private String name;
    
    @Schema(description = "상태", example = "ACTIVE")
    private String status;
}

