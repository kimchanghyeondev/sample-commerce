package com.toycommerce.admin.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    private String name;
    
    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
    private String description;
    
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;
    
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;
}

