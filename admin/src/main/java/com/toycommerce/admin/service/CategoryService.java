package com.toycommerce.admin.service;

import com.toycommerce.admin.dto.category.CategoryResponse;
import com.toycommerce.admin.repository.CategoryRepository;
import com.toycommerce.common.entity.category.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 전체 카테고리 목록 조회 (트리 구조)
     */
    public List<CategoryResponse> getAllCategories() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return rootCategories.stream()
                .map(CategoryResponse::fromWithChildren)
                .toList();
    }

    /**
     * 활성화된 카테고리 목록 조회
     */
    public List<CategoryResponse> getEnabledCategories() {
        List<Category> categories = categoryRepository.findAllEnabled();
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }

    /**
     * 카테고리 상세 조회
     */
    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        return CategoryResponse.fromWithChildren(category);
    }

    /**
     * 하위 카테고리 목록 조회
     */
    public List<CategoryResponse> getChildCategories(Long parentId) {
        List<Category> children = categoryRepository.findByParentIdOrderByDisplayOrder(parentId);
        return children.stream()
                .map(CategoryResponse::from)
                .toList();
    }
}

