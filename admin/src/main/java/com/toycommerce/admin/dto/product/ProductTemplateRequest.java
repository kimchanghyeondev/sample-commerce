package com.toycommerce.admin.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTemplateRequest {
    
    @NotBlank(message = "상품 템플릿명은 필수입니다.")
    @Size(max = 100, message = "상품 템플릿명은 100자 이하여야 합니다.")
    private String name;
    
    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
    private String description;
    
    /**
     * 연결할 카테고리 ID 목록
     */
    private List<Long> categoryIds;
}

