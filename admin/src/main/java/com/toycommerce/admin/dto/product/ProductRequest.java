package com.toycommerce.admin.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotNull(message = "상품 템플릿 ID는 필수입니다.")
    private Long productTemplateId;
    
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    private String name;
    
    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
    private String description;
    
    @NotBlank(message = "SKU는 필수입니다.")
    @Size(max = 50, message = "SKU는 50자 이하여야 합니다.")
    private String sku;
    
    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
    
    @NotNull(message = "재고는 필수입니다.")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;
    
    /**
     * 상품 옵션 그룹 목록 (상품 생성 시 함께 생성)
     */
    private List<OptionGroupRequest> optionGroups;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionGroupRequest {
        
        @NotBlank(message = "옵션 그룹명은 필수입니다.")
        private String name;
        
        /**
         * 옵션 목록
         */
        private List<String> options;
    }
}

