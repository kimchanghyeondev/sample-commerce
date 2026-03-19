package com.toycommerce.user.dto;

import com.toycommerce.common.entity.cart.CartItem;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.store.Store;
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
    
    // 상품 정보
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
    
    @Schema(description = "상품명", example = "프리미엄 활전복")
    private String productName;
    
    @Schema(description = "상품 단가", example = "30000")
    private Integer productPrice;
    
    @Schema(description = "상품 대표 이미지 URL", example = "http://localhost:8082/uploads/2023/10/26/image.jpg")
    private String productImageUrl;
    
    // 상품 옵션 정보
    @Schema(description = "상품 옵션 ID", example = "1")
    private Long productOptionId;
    
    @Schema(description = "상품 옵션명", example = "1kg")
    private String productOptionName;
    
    @Schema(description = "상품 옵션 그룹명", example = "중량")
    private String productOptionGroupName;
    
    // 점포 정보 (내부 처리용)
    @Schema(hidden = true)
    private Long storeId;
    
    @Schema(hidden = true)
    private String storeName;
    
    @Schema(hidden = true)
    private Integer storeShippingFee;
    
    @Schema(hidden = true)
    private Integer storeFreeShippingThreshold;
    
    // 수량 및 금액
    @Schema(description = "수량", example = "2")
    private Integer quantity;
    
    @Schema(description = "합계 금액", example = "60000")
    private Integer totalPrice;

    public static CartItemDto from(CartItem cartItem) {
        return from(cartItem, null);
    }

    public static CartItemDto from(CartItem cartItem, String primaryImageUrl) {
        Product product = cartItem.getProductOption().getProductOptionGroup().getProduct();
        Store store = product.getStore();
        
        Long storeId = null;
        String storeName = "기타";
        Integer shippingFee = 0;
        Integer freeShippingThreshold = null;
        
        if (store != null) {
            storeId = store.getId();
            storeName = store.getName();
            shippingFee = store.getDefaultShippingFee();
            freeShippingThreshold = store.getFreeShippingThreshold();
        }
        
        return CartItemDto.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productImageUrl(primaryImageUrl)
                .productOptionId(cartItem.getProductOption().getId())
                .productOptionName(cartItem.getProductOption().getName())
                .productOptionGroupName(cartItem.getProductOption().getProductOptionGroup().getName())
                .storeId(storeId)
                .storeName(storeName)
                .storeShippingFee(shippingFee)
                .storeFreeShippingThreshold(freeShippingThreshold)
                .quantity(cartItem.getQuantity())
                .totalPrice(product.getPrice() * cartItem.getQuantity())
                .build();
    }
}
