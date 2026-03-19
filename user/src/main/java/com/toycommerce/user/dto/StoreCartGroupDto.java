package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "점포별 장바구니 그룹 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCartGroupDto {
    
    @Schema(description = "점포 ID", example = "1")
    private Long storeId;
    
    @Schema(description = "점포명", example = "창현산지수산")
    private String storeName;
    
    @Schema(description = "배송비", example = "3000")
    private Integer shippingFee;
    
    @Schema(description = "무료배송 기준 금액", example = "50000")
    private Integer freeShippingThreshold;
    
    @Schema(description = "해당 점포 상품 목록")
    private List<CartItemDto> items;
    
    @Schema(description = "해당 점포 상품 수량 합계", example = "3")
    private Integer itemCount;
    
    @Schema(description = "해당 점포 상품 금액 소계", example = "75000")
    private Integer subtotal;
    
    @Schema(description = "적용 배송비 (무료배송 기준 충족 시 0)", example = "0")
    private Integer appliedShippingFee;
    
    /**
     * 무료배송 조건 충족 여부
     */
    public boolean isFreeShipping() {
        return freeShippingThreshold != null && subtotal >= freeShippingThreshold;
    }
    
    /**
     * 무료배송까지 남은 금액
     */
    public Integer getAmountToFreeShipping() {
        if (freeShippingThreshold == null) return null;
        int remaining = freeShippingThreshold - subtotal;
        return remaining > 0 ? remaining : 0;
    }
}

