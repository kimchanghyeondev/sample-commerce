package com.toycommerce.admin.dto.product;

import com.toycommerce.common.entity.attachment.AttachmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "이미지 업로드 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {

    @Schema(description = "Base64 인코딩된 이미지 데이터", example = "data:image/jpeg;base64,/9j/4AAQ...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이미지 데이터는 필수입니다.")
    private String base64Data;

    @Schema(description = "원본 파일명", example = "product_image.jpg")
    private String originalName;

    @Schema(description = "첨부파일 타입", example = "PRODUCT_MAIN")
    private AttachmentType attachmentType;

    @Schema(description = "대표 이미지 여부", example = "false")
    private Boolean isPrimary;

    @Schema(description = "대체 텍스트 (접근성용)", example = "프리미엄 활전복 이미지")
    private String altText;
}

