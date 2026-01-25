package com.toycommerce.common.entity.attachment;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 첨부파일 엔티티 - 순수 파일 정보만 관리
 * 각 도메인과의 연결은 매핑 테이블(CategoryAttachment, ProductAttachment 등)에서 관리
 */
@Entity
@Table(name = "attachment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 원본 파일명
     */
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    /**
     * 저장된 파일명 (UUID 등으로 변환된 이름)
     */
    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    /**
     * 파일 저장 경로
     */
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 파일 URL (CDN 등)
     */
    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    /**
     * 파일 MIME 타입 (image/jpeg, image/png 등)
     */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /**
     * 파일 크기 (bytes)
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 파일 확장자
     */
    @Column(name = "file_extension", length = 20)
    private String fileExtension;

    /**
     * 이미지 너비 (이미지 파일인 경우)
     */
    @Column(name = "width")
    private Integer width;

    /**
     * 이미지 높이 (이미지 파일인 경우)
     */
    @Column(name = "height")
    private Integer height;

    /**
     * 대체 텍스트 (접근성용)
     */
    @Column(name = "alt_text", length = 255)
    private String altText;

    /**
     * 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private EntityStatus status = EntityStatus.ACTIVE;

    /**
     * 파일 URL 업데이트
     */
    public void updateFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 대체 텍스트 업데이트
     */
    public void updateAltText(String altText) {
        this.altText = altText;
    }

    /**
     * 상태 변경
     */
    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

    /**
     * 삭제 (소프트 삭제)
     */
    public void delete() {
        this.status = EntityStatus.DELETED;
    }
}

