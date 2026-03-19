package com.toycommerce.common.entity.store;

/**
 * 입점업체 상태
 */
public enum StoreStatus {
    PENDING,      // 승인 대기
    APPROVED,     // 승인됨 (활성)
    REJECTED,     // 승인 거절
    SUSPENDED,    // 정지 (일시)
    CLOSED        // 폐업
}

