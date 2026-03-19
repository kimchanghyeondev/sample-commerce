package com.toycommerce.common.entity.store;

/**
 * 등급 변경 사유
 */
public enum GradeChangeReason {
    INITIAL,          // 최초 등록
    PROMOTION,        // 승급
    DEMOTION,         // 강등
    ADMIN_ADJUST,     // 관리자 조정
    CONTRACT_RENEWAL  // 계약 갱신
}

