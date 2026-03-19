package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 점포 등급 변경 이력
 */
@Entity
@Table(name = "store_grade_history", indexes = {
        @Index(name = "idx_store_grade_history_store", columnList = "store_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreGradeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_grade_id")
    private StoreGrade previousGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_grade_id", nullable = false)
    private StoreGrade newGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_reason", nullable = false, length = 30)
    private GradeChangeReason changeReason;

    @Column(name = "memo", length = 500)
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;
}

