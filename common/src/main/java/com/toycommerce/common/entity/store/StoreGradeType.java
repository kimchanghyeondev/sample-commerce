package com.toycommerce.common.entity.store;

/**
 * 점포 등급 타입
 */
public enum StoreGradeType {
    BRONZE("브론즈"),
    SILVER("실버"),
    GOLD("골드"),
    PLATINUM("플래티넘"),
    DIAMOND("다이아몬드");

    private final String displayName;

    StoreGradeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

