package com.toycommerce.common.entity.store;

/**
 * 정산 상태
 */
public enum SettlementStatus {
    PENDING,      // 정산 대기
    PROCESSING,   // 정산 처리 중
    COMPLETED,    // 정산 완료 (지급 완료)
    FAILED,       // 정산 실패
    CANCELLED     // 정산 취소
}

