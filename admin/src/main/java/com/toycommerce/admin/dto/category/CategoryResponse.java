package com.toycommerce.admin.dto.category;

import com.toycommerce.common.entity.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean enabled;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> children;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .enabled(category.getEnabled())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .children(new ArrayList<>())
                .build();
    }

    public static CategoryResponse fromWithChildren(Category category) {
        List<CategoryResponse> childResponses = category.getChildren() != null
                ? category.getChildren().stream()
                    .map(CategoryResponse::fromWithChildren)
                    .toList()
                : new ArrayList<>();

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .enabled(category.getEnabled())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .children(childResponses)
                .build();
    }
}

