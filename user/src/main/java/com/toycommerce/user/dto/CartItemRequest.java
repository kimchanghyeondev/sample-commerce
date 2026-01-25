package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "장바구니 아이템 추가 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    
    @Schema(description = "상품 옵션 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "상품 옵션 ID는 필수입니다.")
    private Long productOptionId;
    
    @Schema(description = "수량", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity;
}

