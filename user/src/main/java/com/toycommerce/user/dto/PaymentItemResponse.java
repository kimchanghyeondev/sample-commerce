package com.toycommerce.user.dto;

import com.toycommerce.common.entity.payment.PaymentItem;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "결제 품목 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentItemResponse {

    @Schema(description = "결제 품목 ID", example = "1")
    private Long id;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "프리미엄 활전복")
    private String productName;

    @Schema(description = "상품 옵션 ID", example = "1")
    private Long productOptionId;

    @Schema(description = "상품 옵션명", example = "1kg")
    private String productOptionName;

    @Schema(description = "상품 옵션 그룹명", example = "중량")
    private String productOptionGroupName;

    @Schema(description = "수량", example = "2")
    private Integer quantity;

    @Schema(description = "원가", example = "30000")
    private BigDecimal originPrice;

    @Schema(description = "할인 후 총 금액", example = "55000")
    private BigDecimal totalDiscountedPrice;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static PaymentItemResponse from(PaymentItem paymentItem) {
        ProductOption productOption = paymentItem.getProductOption();
        Product product = productOption.getProductOptionGroup().getProduct();

        return PaymentItemResponse.builder()
                .id(paymentItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productOptionId(productOption.getId())
                .productOptionName(productOption.getName())
                .productOptionGroupName(productOption.getProductOptionGroup().getName())
                .quantity(paymentItem.getQuantity())
                .originPrice(paymentItem.getOriginPrice())
                .totalDiscountedPrice(paymentItem.getTotalDiscountedPrice())
                .createdAt(paymentItem.getCreatedAt())
                .updatedAt(paymentItem.getUpdatedAt())
                .build();
    }
}

