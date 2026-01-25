package com.toycommerce.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductDetailDto {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private Integer price;
    private Integer stock;
    private String status;
    private Long productTemplateId;
    private String productTemplateName;
    private List<ProductOptionGroupDto> optionGroups;
}

