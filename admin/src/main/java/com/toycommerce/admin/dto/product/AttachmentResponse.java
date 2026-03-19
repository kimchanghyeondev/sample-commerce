package com.toycommerce.admin.dto.product;

import com.toycommerce.common.entity.attachment.Attachment;
import com.toycommerce.common.entity.attachment.AttachmentType;
import com.toycommerce.common.entity.attachment.ProductAttachment;
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
public class AttachmentResponse {

    @Schema(description = "첨부파일 ID", example = "1")
    private Long id;

    @Schema(description = "원본 파일명", example = "product_image.jpg")
    private String originalName;

    @Schema(description = "저장된 파일명", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg")
    private String storedName;

    @Schema(description = "파일 URL", example = "http://localhost:8082/uploads/2026/01/25/a1b2c3d4.jpg")
    private String fileUrl;

    @Schema(description = "파일 타입", example = "image/jpeg")
    private String contentType;

    @Schema(description = "파일 크기 (bytes)", example = "102400")
    private Long fileSize;

    @Schema(description = "파일 확장자", example = "jpg")
    private String fileExtension;

    @Schema(description = "이미지 너비", example = "800")
    private Integer width;

    @Schema(description = "이미지 높이", example = "600")
    private Integer height;

    @Schema(description = "대체 텍스트", example = "프리미엄 활전복 이미지")
    private String altText;

    @Schema(description = "첨부파일 타입", example = "PRODUCT_MAIN")
    private AttachmentType attachmentType;

    @Schema(description = "정렬 순서", example = "0")
    private Integer sortOrder;

    @Schema(description = "대표 이미지 여부", example = "true")
    private Boolean isPrimary;

    public static AttachmentResponse from(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalName(attachment.getOriginalName())
                .storedName(attachment.getStoredName())
                .fileUrl(attachment.getFileUrl())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .fileExtension(attachment.getFileExtension())
                .width(attachment.getWidth())
                .height(attachment.getHeight())
                .altText(attachment.getAltText())
                .build();
    }

    public static AttachmentResponse from(Attachment attachment, ProductAttachment productAttachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalName(attachment.getOriginalName())
                .storedName(attachment.getStoredName())
                .fileUrl(attachment.getFileUrl())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .fileExtension(attachment.getFileExtension())
                .width(attachment.getWidth())
                .height(attachment.getHeight())
                .altText(attachment.getAltText())
                .attachmentType(productAttachment.getAttachmentType())
                .sortOrder(productAttachment.getSortOrder())
                .isPrimary(productAttachment.getIsPrimary())
                .build();
    }
}

