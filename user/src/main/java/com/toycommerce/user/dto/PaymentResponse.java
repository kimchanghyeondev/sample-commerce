package com.toycommerce.user.dto;

import com.toycommerce.common.entity.payment.Payment;
import com.toycommerce.common.entity.payment.PaymentItem;
import com.toycommerce.common.entity.payment.PaymentMethod;
import com.toycommerce.common.entity.payment.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "결제 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    @Schema(description = "결제 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "결제 상태", example = "PENDING")
    private PaymentStatus status;

    @Schema(description = "결제 수단", example = "CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "총 상품 금액", example = "150000")
    private BigDecimal totalAmount;

    @Schema(description = "총 할인 금액", example = "5000")
    private BigDecimal totalDiscountAmount;

    @Schema(description = "배송비", example = "3000")
    private BigDecimal shippingFee;

    @Schema(description = "최종 결제 금액", example = "148000")
    private BigDecimal finalAmount;

    @Schema(description = "결제 고유 키", example = "payment_key_12345")
    private String paymentKey;

    @Schema(description = "거래 ID", example = "transaction_12345")
    private String transactionId;

    @Schema(description = "결제 품목 목록")
    private List<PaymentItemResponse> paymentItems;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static PaymentResponse from(Payment payment) {
        List<PaymentItemResponse> items = payment.getPaymentItems().stream()
                .map(PaymentItemResponse::from)
                .toList();

        return PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUser().getId())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .totalAmount(payment.getTotalAmount())
                .totalDiscountAmount(payment.getTotalDiscountAmount())
                .shippingFee(payment.getShippingFee())
                .finalAmount(payment.getFinalAmount())
                .paymentKey(payment.getPaymentKey())
                .transactionId(payment.getTransactionId())
                .paymentItems(items)
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

