package com.toycommerce.gateway.dto;

import com.toycommerce.common.entity.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean enabled;
    private Long parentId;
    private String parentName;
    private List<CategoryDto> children;
    private Integer depth;

    public static CategoryDto from(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto.CategoryDtoBuilder builder = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .enabled(category.getEnabled())
                .depth(category.getDepth());

        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId())
                   .parentName(category.getParent().getName());
        }

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            builder.children(category.getChildren().stream()
                    .map(CategoryDto::from)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}

