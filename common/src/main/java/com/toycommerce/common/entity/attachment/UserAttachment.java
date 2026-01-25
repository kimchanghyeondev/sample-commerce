package com.toycommerce.common.entity.attachment;

import com.toycommerce.common.entity.BaseEntity;
import com.toycommerce.common.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자-첨부파일 매핑 엔티티
 */
@Entity
@Table(name = "user_attachment",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_user_attachment_type",
           columnNames = {"user_id", "attachment_type"}
       ))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id", nullable = false)
    private Attachment attachment;

    /**
     * 첨부파일 유형 (PROFILE 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false, length = 50)
    private AttachmentType attachmentType;

    /**
     * 첨부파일 변경
     */
    public void updateAttachment(Attachment attachment) {
        this.attachment = attachment;
    }
}

