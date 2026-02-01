package com.toycommerce.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "결제 추가 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @Schema(description = "장바구니 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "장바구니 ID는 필수입니다.")
    private Long cartId;

    @Schema(description = "결제 품목 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    // 필수 값
    private List<PaymentItemRequest> paymentItems;

    @Schema(description = "결제 예정금액", example = "150000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalPrice;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentItemRequest {

        @Schema(description = "상품 옵션 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "상품 옵션 ID는 필수입니다.")
        private Long productOptionId;

        @Schema(description = "수량", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        private Integer quantity;

    }

}

