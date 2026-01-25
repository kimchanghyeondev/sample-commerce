package com.toycommerce.user.dto;

import com.toycommerce.common.entity.cart.Cart;
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
    
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "사용자명", example = "user")
    private String username;
    
    @Schema(description = "장바구니 아이템 목록")
    private List<CartItemDto> items;
    
    @Schema(description = "총 수량", example = "5")
    private Integer totalQuantity;
    
    @Schema(description = "총 가격", example = "150000")
    private Integer totalPrice;

    public static CartDto from(Cart cart) {
        List<CartItemDto> items = cart.getCartItems().stream()
                .map(CartItemDto::from)
                .toList();

        int totalQuantity = items.stream()
                .mapToInt(CartItemDto::getQuantity)
                .sum();

        int totalPrice = items.stream()
                .mapToInt(CartItemDto::getTotalPrice)
                .sum();

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .username(cart.getUser().getUsername())
                .items(items)
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .build();
    }
}

