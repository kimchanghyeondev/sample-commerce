package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "상품 옵션 그룹 응답 DTO")
@Data
@Builder
public class ProductOptionGroupDto {
    @Schema(description = "옵션 그룹 ID", example = "1")
    private Long id;
    
    @Schema(description = "옵션 그룹명", example = "중량")
    private String name;
    
    @Schema(description = "상태", example = "ACTIVE")
    private String status;
    
    @Schema(description = "옵션 목록")
    private List<ProductOptionDto> options;
}

