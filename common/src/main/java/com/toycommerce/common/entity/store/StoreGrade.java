package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 점포 등급
 */
@Entity
@Table(name = "store_grade")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreGrade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type", nullable = false, unique = true, length = 20)
    private StoreGradeType gradeType;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    public String getDisplayName() {
        return gradeType != null ? gradeType.getDisplayName() : null;
    }

    public void updateCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }
}

