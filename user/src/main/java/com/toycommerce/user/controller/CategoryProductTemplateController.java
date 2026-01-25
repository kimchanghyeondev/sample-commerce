package com.toycommerce.user.controller;

import com.toycommerce.user.dto.CategoryProductTemplateDto;
import com.toycommerce.user.dto.CategoryWithProductTemplatesDto;
import com.toycommerce.user.service.CategoryProductTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/category-product-templates")
@RequiredArgsConstructor
public class CategoryProductTemplateController {

    private final CategoryProductTemplateService categoryProductTemplateService;

    @GetMapping("/grouped")
    public ResponseEntity<List<CategoryWithProductTemplatesDto>> getGroupedByCategory() {
        return ResponseEntity.ok(categoryProductTemplateService.getGroupedByCategory());
    }

    @GetMapping("/category/{categoryId}/grouped")
    public ResponseEntity<CategoryWithProductTemplatesDto> getByCategoryIdGrouped(@PathVariable Long categoryId) {
        CategoryWithProductTemplatesDto grouped = categoryProductTemplateService.getByCategoryIdGrouped(categoryId);
        if (grouped == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grouped);
    }

    @GetMapping
    public ResponseEntity<List<CategoryProductTemplateDto>> getAllEnabled() {
        return ResponseEntity.ok(categoryProductTemplateService.getAllEnabled());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CategoryProductTemplateDto>> getByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryProductTemplateService.getByCategoryId(categoryId));
    }

    @GetMapping("/parent/{parentCategoryId}")
    public ResponseEntity<List<CategoryProductTemplateDto>> getByParentCategoryId(@PathVariable Long parentCategoryId) {
        return ResponseEntity.ok(categoryProductTemplateService.getByParentCategoryId(parentCategoryId));
    }
}

