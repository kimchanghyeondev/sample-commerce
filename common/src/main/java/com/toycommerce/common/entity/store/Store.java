package com.toycommerce.common.entity.store;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.common.Address;
import com.toycommerce.common.entity.common.ContactInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 입점업체
 */
@Entity
@Table(name = "store", indexes = {
        @Index(name = "idx_store_status", columnList = "status"),
        @Index(name = "idx_store_business_number", columnList = "business_number")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== 기본 정보 =====
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "business_number", unique = true, length = 20)
    private String businessNumber;

    @Column(name = "representative_name", length = 50)
    private String representativeName;

    @Column(name = "description", length = 2000)
    private String description;

    // ===== 연락처 (Embedded) =====
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "phone", column = @Column(name = "phone")),
            @AttributeOverride(name = "email", column = @Column(name = "email"))
    })
    private ContactInfo contactInfo;

    // ===== 사업장 주소 (Embedded) =====
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode", column = @Column(name = "biz_zip_code")),
            @AttributeOverride(name = "address", column = @Column(name = "biz_address")),
            @AttributeOverride(name = "addressDetail", column = @Column(name = "biz_address_detail"))
    })
    private Address businessAddress;

    // ===== 출고지 주소 (Embedded) =====
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipCode", column = @Column(name = "ship_zip_code")),
            @AttributeOverride(name = "address", column = @Column(name = "ship_address")),
            @AttributeOverride(name = "addressDetail", column = @Column(name = "ship_address_detail"))
    })
    private Address shippingAddress;

    // ===== 배송 정책 =====
    @Column(name = "default_shipping_fee")
    @Builder.Default
    private Integer defaultShippingFee = 0;

    @Column(name = "free_shipping_threshold")
    private Integer freeShippingThreshold;

    // ===== 상태 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StoreStatus status = StoreStatus.PENDING;

    // ===== 등급 =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_grade_id")
    private StoreGrade storeGrade;

    // ===== 현재 유효 계약 =====
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_contract_id")
    private StoreContract activeContract;

    // ===== 업데이트 메서드 =====
    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void updateBusinessAddress(Address businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void updateShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void updateShippingPolicy(Integer defaultShippingFee, Integer freeShippingThreshold) {
        this.defaultShippingFee = defaultShippingFee;
        this.freeShippingThreshold = freeShippingThreshold;
    }

    public void updateStatus(StoreStatus status) {
        this.status = status;
    }

    public void updateStoreGrade(StoreGrade storeGrade) {
        this.storeGrade = storeGrade;
    }

    public void updateActiveContract(StoreContract activeContract) {
        this.activeContract = activeContract;
    }

    public void approve() {
        this.status = StoreStatus.APPROVED;
    }

    public void reject() {
        this.status = StoreStatus.REJECTED;
    }

    public void suspend() {
        this.status = StoreStatus.SUSPENDED;
    }

    public void close() {
        this.status = StoreStatus.CLOSED;
    }
}

