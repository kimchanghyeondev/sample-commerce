package com.toycommerce.admin.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptionGroupRequest {
    
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;
    
    @NotBlank(message = "옵션 그룹명은 필수입니다.")
    private String name;
    
    /**
     * 옵션 목록 (옵션 그룹 생성 시 함께 생성)
     */
    private List<String> options;
}

