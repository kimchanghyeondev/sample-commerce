package com.toycommerce.user.controller;

import com.toycommerce.user.dto.CategoryProductTemplateDto;
import com.toycommerce.user.dto.CategoryWithProductTemplatesDto;
import com.toycommerce.user.service.CategoryProductTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리-상품 템플릿 매핑", description = "카테고리별 상품 템플릿 조회 API")
@RestController
@RequestMapping("/api/user/category-product-templates")
@RequiredArgsConstructor
public class CategoryProductTemplateController {

    private final CategoryProductTemplateService categoryProductTemplateService;

    @Operation(summary = "카테고리별 상품 템플릿 그룹 조회", description = "모든 카테고리를 상품 템플릿 목록과 함께 그룹화하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/grouped")
    public ResponseEntity<List<CategoryWithProductTemplatesDto>> getGroupedByCategory() {
        return ResponseEntity.ok(categoryProductTemplateService.getGroupedByCategory());
    }

    @Operation(summary = "특정 카테고리의 상품 템플릿 그룹 조회", description = "특정 카테고리의 상품 템플릿을 그룹화하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @GetMapping("/category/{categoryId}/grouped")
    public ResponseEntity<CategoryWithProductTemplatesDto> getByCategoryIdGrouped(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        CategoryWithProductTemplatesDto grouped = categoryProductTemplateService.getByCategoryIdGrouped(categoryId);
        if (grouped == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grouped);
    }

    @Operation(summary = "전체 활성화된 매핑 조회", description = "모든 활성화된 카테고리-상품 템플릿 매핑을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<CategoryProductTemplateDto>> getAllEnabled() {
        return ResponseEntity.ok(categoryProductTemplateService.getAllEnabled());
    }

    @Operation(summary = "카테고리별 매핑 조회", description = "특정 카테고리의 상품 템플릿 매핑을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CategoryProductTemplateDto>> getByCategoryId(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryProductTemplateService.getByCategoryId(categoryId));
    }

    @Operation(summary = "부모 카테고리별 매핑 조회", description = "특정 부모 카테고리의 하위 카테고리 상품 템플릿 매핑을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/parent/{parentCategoryId}")
    public ResponseEntity<List<CategoryProductTemplateDto>> getByParentCategoryId(
            @Parameter(description = "부모 카테고리 ID") @PathVariable Long parentCategoryId) {
        return ResponseEntity.ok(categoryProductTemplateService.getByParentCategoryId(parentCategoryId));
    }
}

