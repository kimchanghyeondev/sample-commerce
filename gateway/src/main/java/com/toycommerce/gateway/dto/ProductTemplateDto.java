package com.toycommerce.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductTemplateDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private Integer sortOrder;
    private Long mappingId;
}

