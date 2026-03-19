package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 업체 공지/휴무 안내
 */
@Entity
@Table(name = "store_notice", indexes = {
        @Index(name = "idx_store_notice_store", columnList = "store_id"),
        @Index(name = "idx_store_notice_type", columnList = "notice_type"),
        @Index(name = "idx_store_notice_date", columnList = "start_date, end_date")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type", nullable = false, length = 30)
    private StoreNoticeType noticeType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", length = 2000)
    private String content;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ===== 업데이트 메서드 =====
    public void updateNotice(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updatePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isCurrentlyActive() {
        if (!isActive) return false;
        LocalDate today = LocalDate.now();
        if (startDate != null && today.isBefore(startDate)) return false;
        if (endDate != null && today.isAfter(endDate)) return false;
        return true;
    }
}

