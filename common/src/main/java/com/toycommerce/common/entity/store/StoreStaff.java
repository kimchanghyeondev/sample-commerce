package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 업체 직원/관리자
 */
@Entity
@Table(name = "store_staff", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "user_id"})
}, indexes = {
        @Index(name = "idx_store_staff_store", columnList = "store_id"),
        @Index(name = "idx_store_staff_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStaff extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_role", nullable = false, length = 20)
    private StoreStaffRole staffRole;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    // ===== 업데이트 메서드 =====
    public void updateStaffRole(StoreStaffRole staffRole) {
        this.staffRole = staffRole;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

    public void deactivate() {
        this.status = EntityStatus.DELETED;
    }
}

