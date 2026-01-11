package com.toycommerce.gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductOptionDto {
    private Long id;
    private String name;
    private String status;
}

