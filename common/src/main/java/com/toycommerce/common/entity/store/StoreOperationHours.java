package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 요일별 운영 시간
 */
@Entity
@Table(name = "store_operation_hours", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "day_of_week"})
}, indexes = {
        @Index(name = "idx_store_operation_hours_store", columnList = "store_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOperationHours extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 15)
    private DayOfWeek dayOfWeek;

    @Column(name = "is_open", nullable = false)
    @Builder.Default
    private Boolean isOpen = true;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "break_start")
    private LocalTime breakStart;

    @Column(name = "break_end")
    private LocalTime breakEnd;

    // ===== 업데이트 메서드 =====
    public void updateOperationHours(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public void updateBreakTime(LocalTime breakStart, LocalTime breakEnd) {
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isCurrentlyOpen() {
        if (!isOpen) return false;
        LocalTime now = LocalTime.now();
        if (openTime != null && closeTime != null) {
            if (now.isBefore(openTime) || now.isAfter(closeTime)) {
                return false;
            }
        }
        if (breakStart != null && breakEnd != null) {
            if (now.isAfter(breakStart) && now.isBefore(breakEnd)) {
                return false;
            }
        }
        return true;
    }
}

