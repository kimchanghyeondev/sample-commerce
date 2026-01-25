package com.toycommerce.user.dto;

import com.toycommerce.common.entity.cart.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "장바구니 아이템 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    @Schema(description = "장바구니 아이템 ID", example = "1")
    private Long id;
    
    @Schema(description = "상품 옵션 ID", example = "1")
    private Long productOptionId;
    
    @Schema(description = "상품 옵션명", example = "1kg")
    private String productOptionName;
    
    @Schema(description = "상품 옵션 그룹명", example = "중량")
    private String productOptionGroupName;
    
    @Schema(description = "상품명", example = "프리미엄 활전복")
    private String productName;
    
    @Schema(description = "상품 단가", example = "30000")
    private Integer productPrice;
    
    @Schema(description = "수량", example = "2")
    private Integer quantity;
    
    @Schema(description = "합계 금액", example = "60000")
    private Integer totalPrice;

    public static CartItemDto from(CartItem cartItem) {
        return CartItemDto.builder()
                .id(cartItem.getId())
                .productOptionId(cartItem.getProductOption().getId())
                .productOptionName(cartItem.getProductOption().getName())
                .productOptionGroupName(cartItem.getProductOption().getProductOptionGroup().getName())
                .productName(cartItem.getProductOption().getProductOptionGroup().getProduct().getName())
                .productPrice(cartItem.getProductOption().getProductOptionGroup().getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getProductOption().getProductOptionGroup().getProduct().getPrice() * cartItem.getQuantity())
                .build();
    }
}

