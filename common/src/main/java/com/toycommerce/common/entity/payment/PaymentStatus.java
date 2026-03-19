package com.toycommerce.common.entity.payment;

/**
 * 결제 상태
 */
public enum PaymentStatus {
    PENDING,      // 결제 대기
    COMPLETED,    // 결제 완료
    CANCELLED,    // 결제 취소
    FAILED,       // 결제 실패
    REFUNDED      // 환불 완료
}

