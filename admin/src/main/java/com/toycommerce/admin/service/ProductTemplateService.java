package com.toycommerce.admin.service;

import com.toycommerce.admin.dto.common.PageResponse;
import com.toycommerce.admin.dto.product.ProductTemplateRequest;
import com.toycommerce.admin.dto.product.ProductTemplateResponse;
import com.toycommerce.admin.repository.CategoryProductTemplateMappingRepository;
import com.toycommerce.admin.repository.CategoryRepository;
import com.toycommerce.admin.repository.ProductTemplateRepository;
import com.toycommerce.common.entity.category.Category;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.ProductTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductTemplateService {

    private final ProductTemplateRepository productTemplateRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryProductTemplateMappingRepository mappingRepository;

    /**
     * 상품 템플릿 목록 조회 (페이징)
     */
    public PageResponse<ProductTemplateResponse> getProductTemplates(Pageable pageable) {
        Page<ProductTemplate> page = productTemplateRepository.findAllActive(pageable);
        return PageResponse.from(page, ProductTemplateResponse::from);
    }

    /**
     * 상품 템플릿 검색
     */
    public PageResponse<ProductTemplateResponse> searchProductTemplates(String keyword, Pageable pageable) {
        Page<ProductTemplate> page = productTemplateRepository.searchByName(keyword, pageable);
        return PageResponse.from(page, ProductTemplateResponse::from);
    }

    /**
     * 상품 템플릿 상세 조회
     */
    public ProductTemplateResponse getProductTemplate(Long id) {
        ProductTemplate template = findById(id);
        return ProductTemplateResponse.from(template);
    }

    /**
     * 상품 템플릿 생성
     */
    @Transactional
    public ProductTemplateResponse createProductTemplate(ProductTemplateRequest request) {
        // 중복 이름 체크
        if (productTemplateRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 상품 템플릿명입니다: " + request.getName());
        }

        // 템플릿 생성
        ProductTemplate template = ProductTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(EntityStatus.ACTIVE)
                .build();

        ProductTemplate savedTemplate = productTemplateRepository.save(template);
        log.info("상품 템플릿 생성: id={}, name={}", savedTemplate.getId(), savedTemplate.getName());

        // 카테고리 매핑
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            createCategoryMappings(savedTemplate, request.getCategoryIds());
        }

        return ProductTemplateResponse.from(savedTemplate);
    }

    /**
     * 상품 템플릿 수정
     */
    @Transactional
    public ProductTemplateResponse updateProductTemplate(Long id, ProductTemplateRequest request) {
        ProductTemplate template = findById(id);

        // 이름 변경 시 중복 체크
        if (!template.getName().equals(request.getName()) 
                && productTemplateRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 상품 템플릿명입니다: " + request.getName());
        }

        template.updateName(request.getName());
        template.updateDescription(request.getDescription());

        // 카테고리 매핑 갱신
        if (request.getCategoryIds() != null) {
            updateCategoryMappings(template, request.getCategoryIds());
        }

        log.info("상품 템플릿 수정: id={}, name={}", template.getId(), template.getName());
        return ProductTemplateResponse.from(template);
    }

    /**
     * 상품 템플릿 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteProductTemplate(Long id) {
        ProductTemplate template = findById(id);
        template.updateStatus(EntityStatus.DELETED);
        log.info("상품 템플릿 삭제: id={}", id);
    }

    /**
     * 상품 템플릿 상태 변경
     */
    @Transactional
    public ProductTemplateResponse updateStatus(Long id, EntityStatus status) {
        ProductTemplate template = findById(id);
        template.updateStatus(status);
        log.info("상품 템플릿 상태 변경: id={}, status={}", id, status);
        return ProductTemplateResponse.from(template);
    }

    private ProductTemplate findById(Long id) {
        return productTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품 템플릿을 찾을 수 없습니다: " + id));
    }

    private void createCategoryMappings(ProductTemplate template, List<Long> categoryIds) {
        int sortOrder = 0;
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + categoryId));

            CategoryProductTemplateMapping mapping = CategoryProductTemplateMapping.builder()
                    .category(category)
                    .productTemplate(template)
                    .sortOrder(sortOrder++)
                    .build();

            mappingRepository.save(mapping);
        }
    }

    private void updateCategoryMappings(ProductTemplate template, List<Long> categoryIds) {
        // 기존 매핑 삭제
        mappingRepository.deleteByProductTemplateId(template.getId());

        // 새 매핑 생성
        if (!categoryIds.isEmpty()) {
            createCategoryMappings(template, categoryIds);
        }
    }
}

