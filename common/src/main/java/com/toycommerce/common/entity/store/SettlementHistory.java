package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 정산 내역
 */
@Entity
@Table(name = "settlement_history", indexes = {
        @Index(name = "idx_settlement_store", columnList = "store_id"),
        @Index(name = "idx_settlement_date", columnList = "settlement_date"),
        @Index(name = "idx_settlement_status", columnList = "status")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private StoreContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id")
    private StoreBankAccount bankAccount;

    // ===== 정산 기간 =====
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    // ===== 금액 =====
    @Column(name = "total_sales", nullable = false)
    private Long totalSales;

    @Column(name = "total_refund")
    @Builder.Default
    private Long totalRefund = 0L;

    @Column(name = "net_sales", nullable = false)
    private Long netSales;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "commission_amount", nullable = false)
    private Long commissionAmount;

    @Column(name = "settlement_amount", nullable = false)
    private Long settlementAmount;

    // ===== 건수 =====
    @Column(name = "order_count")
    @Builder.Default
    private Integer orderCount = 0;

    @Column(name = "refund_count")
    @Builder.Default
    private Integer refundCount = 0;

    // ===== 상태 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "memo", length = 500)
    private String memo;

    // ===== 업데이트 메서드 =====
    public void updateStatus(SettlementStatus status) {
        this.status = status;
        if (status == SettlementStatus.COMPLETED) {
            this.paidAt = LocalDateTime.now();
        }
    }

    public void markAsCompleted() {
        this.status = SettlementStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed(String memo) {
        this.status = SettlementStatus.FAILED;
        this.memo = memo;
    }
}

