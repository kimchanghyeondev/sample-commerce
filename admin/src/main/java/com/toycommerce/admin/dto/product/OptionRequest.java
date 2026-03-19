package com.toycommerce.admin.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequest {
    
    @NotNull(message = "옵션 그룹 ID는 필수입니다.")
    private Long optionGroupId;
    
    @NotBlank(message = "옵션명은 필수입니다.")
    private String name;
}

