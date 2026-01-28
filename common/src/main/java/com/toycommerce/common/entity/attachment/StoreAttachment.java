package com.toycommerce.common.entity.attachment;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.store.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 입점업체-첨부파일 매핑 엔티티
 */
@Entity
@Table(name = "store_attachment", indexes = {
        @Index(name = "idx_store_attachment_store", columnList = "store_id"),
        @Index(name = "idx_store_attachment_type", columnList = "store_id, attachment_type")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false, length = 50)
    private AttachmentType attachmentType;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    // ===== 업데이트 메서드 =====
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}

