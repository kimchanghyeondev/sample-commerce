package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정산 계좌
 */
@Entity
@Table(name = "store_bank_account", indexes = {
        @Index(name = "idx_store_bank_account_store", columnList = "store_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreBankAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "bank_code", length = 10)
    private String bankCode;

    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false, length = 50)
    private String accountHolder;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    // ===== 업데이트 메서드 =====
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void verify() {
        this.isVerified = true;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

    public void updateAccountInfo(String bankCode, String bankName, String accountNumber, String accountHolder) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.isVerified = false; // 계좌 정보 변경 시 재인증 필요
    }
}

