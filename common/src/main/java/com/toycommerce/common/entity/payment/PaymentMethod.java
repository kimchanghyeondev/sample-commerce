package com.toycommerce.common.entity.payment;

/**
 * 결제 수단
 */
public enum PaymentMethod {
    CARD,              // 신용카드
    BANK_TRANSFER,     // 계좌이체
    VIRTUAL_ACCOUNT,   // 가상계좌
    MOBILE,            // 휴대폰 결제
    POINT,             // 포인트 결제
    COUPON             // 쿠폰 결제
}

