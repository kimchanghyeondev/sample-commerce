package com.toycommerce.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryWithProductTemplatesDto {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private Integer categoryDisplayOrder;
    private Boolean categoryEnabled;
    private List<ProductTemplateDto> productTemplates;
}

