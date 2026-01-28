package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "장바구니 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    @Schema(description = "장바구니 ID", example = "1")
    private Long id;
    
    @Schema(description = "점포별 상품 그룹 목록")
    private List<StoreCartGroupDto> storeGroups;
    
    @Schema(description = "총 상품 수량", example = "5")
    private Integer totalQuantity;
    
    @Schema(description = "총 상품 금액", example = "150000")
    private Integer totalProductPrice;
    
    @Schema(description = "총 배송비", example = "3000")
    private Integer totalShippingFee;
    
    @Schema(description = "총 결제 금액 (상품 + 배송비)", example = "153000")
    private Integer totalPrice;
    
    @Schema(description = "점포 수", example = "2")
    private Integer storeCount;
}
