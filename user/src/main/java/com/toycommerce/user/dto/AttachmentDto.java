package com.toycommerce.user.dto;

import com.toycommerce.common.entity.attachment.Attachment;
import com.toycommerce.common.entity.attachment.AttachmentType;
import com.toycommerce.common.entity.attachment.ProductAttachment;
import com.toycommerce.common.entity.attachment.ProductTemplateAttachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "첨부파일 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDto {

    @Schema(description = "첨부파일 ID", example = "1")
    private Long id;

    @Schema(description = "파일 URL", example = "http://localhost:8082/uploads/2026/01/25/uuid.jpg")
    private String fileUrl;

    @Schema(description = "원본 파일명", example = "product_image.jpg")
    private String originalName;

    @Schema(description = "파일 타입", example = "image/jpeg")
    private String contentType;

    @Schema(description = "대체 텍스트", example = "프리미엄 활전복 이미지")
    private String altText;

    @Schema(description = "첨부파일 타입", example = "PRODUCT_MAIN")
    private AttachmentType attachmentType;

    @Schema(description = "대표 이미지 여부", example = "true")
    private Boolean isPrimary;

    @Schema(description = "정렬 순서", example = "0")
    private Integer sortOrder;

    public static AttachmentDto from(Attachment attachment) {
        return AttachmentDto.builder()
                .id(attachment.getId())
                .fileUrl(attachment.getFileUrl())
                .originalName(attachment.getOriginalName())
                .contentType(attachment.getContentType())
                .altText(attachment.getAltText())
                .build();
    }

    public static AttachmentDto from(ProductAttachment productAttachment) {
        Attachment attachment = productAttachment.getAttachment();
        return AttachmentDto.builder()
                .id(attachment.getId())
                .fileUrl(attachment.getFileUrl())
                .originalName(attachment.getOriginalName())
                .contentType(attachment.getContentType())
                .altText(attachment.getAltText())
                .attachmentType(productAttachment.getAttachmentType())
                .isPrimary(productAttachment.getIsPrimary())
                .sortOrder(productAttachment.getSortOrder())
                .build();
    }

    public static AttachmentDto from(ProductTemplateAttachment templateAttachment) {
        Attachment attachment = templateAttachment.getAttachment();
        return AttachmentDto.builder()
                .id(attachment.getId())
                .fileUrl(attachment.getFileUrl())
                .originalName(attachment.getOriginalName())
                .contentType(attachment.getContentType())
                .altText(attachment.getAltText())
                .attachmentType(templateAttachment.getAttachmentType())
                .isPrimary(templateAttachment.getIsPrimary())
                .sortOrder(templateAttachment.getSortOrder())
                .build();
    }
}

