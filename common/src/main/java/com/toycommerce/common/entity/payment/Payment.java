package com.toycommerce.common.entity.payment;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "payment", cascade = {CascadeType.ALL}, orphanRemoval = true)
    @Builder.Default
    private List<PaymentItem> paymentItems = new ArrayList<>();

    // ===== 금액 정보 =====
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;  // 총 상품 금액

    @Column(name = "total_discount_amount", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;  // 총 할인 금액

    @Column(name = "shipping_fee", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;  // 배송비

    @Column(name = "final_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal finalAmount;  // 최종 결제 금액

    // ===== 결제 정보 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;  // 결제 상태

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;  // 결제 수단

    @Column(name = "payment_key", length = 200)
    private String paymentKey;  // 결제 고유 키 (외부 PG사에서 발급)

    @Column(name = "transaction_id", length = 200)
    private String transactionId;  // 거래 ID

    // ===== 업데이트 메서드 =====
    public void updatePaymentItems(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems;
    }

    public void updateAmounts(BigDecimal totalAmount, BigDecimal totalDiscountAmount, BigDecimal shippingFee) {
        this.totalAmount = totalAmount;
        this.totalDiscountAmount = totalDiscountAmount;
        this.shippingFee = shippingFee;
        this.finalAmount = totalAmount.subtract(totalDiscountAmount).add(shippingFee);
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updatePaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void updatePaymentInfo(String paymentKey, String transactionId) {
        this.paymentKey = paymentKey;
        this.transactionId = transactionId;
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean isCancellable() {
        return this.status == PaymentStatus.PENDING || this.status == PaymentStatus.COMPLETED;
    }
}
