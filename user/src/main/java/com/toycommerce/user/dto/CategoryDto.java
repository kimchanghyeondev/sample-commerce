package com.toycommerce.user.dto;

import com.toycommerce.common.entity.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "카테고리 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @Schema(description = "카테고리 ID", example = "1")
    private Long id;
    
    @Schema(description = "카테고리명", example = "수산물")
    private String name;
    
    @Schema(description = "카테고리 설명", example = "신선한 수산물")
    private String description;
    
    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean enabled;
    
    @Schema(description = "부모 카테고리 ID", example = "null")
    private Long parentId;
    
    @Schema(description = "부모 카테고리명")
    private String parentName;
    
    @Schema(description = "하위 카테고리 목록")
    private List<CategoryDto> children;
    
    @Schema(description = "카테고리 깊이 (0: 루트)", example = "0")
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

