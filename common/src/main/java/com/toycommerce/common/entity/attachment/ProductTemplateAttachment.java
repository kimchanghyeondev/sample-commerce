package com.toycommerce.common.entity.attachment;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.product.ProductTemplate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 템플릿-첨부파일 매핑 엔티티
 */
@Entity
@Table(name = "product_template_attachment",
       indexes = {
           @Index(name = "idx_product_template_attachment_template", columnList = "product_template_id"),
           @Index(name = "idx_product_template_attachment_type", columnList = "product_template_id, attachment_type")
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTemplateAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_template_id", nullable = false)
    private ProductTemplate productTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;

    /**
     * 첨부파일 유형 (MAIN, DETAIL 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false, length = 50)
    private AttachmentType attachmentType;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 대표 이미지 여부
     */
    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;

    /**
     * 대표 이미지 설정
     */
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * 정렬 순서 변경
     */
    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}

