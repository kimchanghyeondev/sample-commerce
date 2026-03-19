package com.toycommerce.admin.controller;

import com.toycommerce.admin.dto.category.CategoryResponse;
import com.toycommerce.admin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 카테고리 조회 API (관리자용)
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 전체 카테고리 목록 조회 (트리 구조)
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * 활성화된 카테고리 목록 조회 (플랫 구조)
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<CategoryResponse>> getEnabledCategories() {
        List<CategoryResponse> categories = categoryService.getEnabledCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * 카테고리 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    /**
     * 하위 카테고리 목록 조회
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable Long parentId) {
        List<CategoryResponse> children = categoryService.getChildCategories(parentId);
        return ResponseEntity.ok(children);
    }
}

