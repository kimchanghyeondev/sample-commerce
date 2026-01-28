package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 입점 계약
 */
@Entity
@Table(name = "store_contract", indexes = {
        @Index(name = "idx_store_contract_store", columnList = "store_id"),
        @Index(name = "idx_store_contract_status", columnList = "status")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreContract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // ===== 계약 기간 =====
    @Column(name = "contract_start_date", nullable = false)
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    // ===== 수수료 (계약 시점 적용 등급) =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_grade_id", nullable = false)
    private StoreGrade storeGrade;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    // ===== 정산 조건 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_cycle", nullable = false, length = 20)
    @Builder.Default
    private SettlementCycle settlementCycle = SettlementCycle.MONTHLY;

    @Column(name = "settlement_day")
    @Builder.Default
    private Integer settlementDay = 15;

    // ===== 상태 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContractStatus status = ContractStatus.ACTIVE;

    @Column(name = "memo", length = 1000)
    private String memo;

    // ===== 업데이트 메서드 =====
    public void updateStatus(ContractStatus status) {
        this.status = status;
    }

    public void terminate() {
        this.status = ContractStatus.TERMINATED;
    }

    public void expire() {
        this.status = ContractStatus.EXPIRED;
    }

    public boolean isActive() {
        return this.status == ContractStatus.ACTIVE;
    }

    public boolean isExpired() {
        return contractEndDate != null && LocalDate.now().isAfter(contractEndDate);
    }
}

