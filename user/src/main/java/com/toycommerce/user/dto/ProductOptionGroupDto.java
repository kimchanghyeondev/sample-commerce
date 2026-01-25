package com.toycommerce.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductOptionGroupDto {
    private Long id;
    private String name;
    private String status;
    private List<ProductOptionDto> options;
}

