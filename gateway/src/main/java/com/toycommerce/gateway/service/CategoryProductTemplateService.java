package com.toycommerce.gateway.service;

import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.gateway.dto.CategoryProductTemplateDto;
import com.toycommerce.gateway.dto.CategoryWithProductTemplatesDto;
import com.toycommerce.gateway.dto.ProductTemplateDto;
import com.toycommerce.gateway.repository.CategoryProductTemplateMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryProductTemplateService {

    private final CategoryProductTemplateMappingRepository mappingRepository;

    @Transactional(readOnly = true)
    public List<CategoryProductTemplateDto> getByCategoryId(Long categoryId) {
        List<CategoryProductTemplateMapping> mappings = mappingRepository.findByCategoryIdWithDetails(categoryId);
        return mappings.stream()
                .map(mapping -> CategoryProductTemplateDto.from(
                        mapping.getCategory(),
                        mapping.getProductTemplate(),
                        mapping.getId(),
                        mapping.getSortOrder()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryProductTemplateDto> getAllEnabled() {
        List<CategoryProductTemplateMapping> mappings = mappingRepository.findAllEnabledWithDetails();
        return mappings.stream()
                .map(mapping -> CategoryProductTemplateDto.from(
                        mapping.getCategory(),
                        mapping.getProductTemplate(),
                        mapping.getId(),
                        mapping.getSortOrder()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryProductTemplateDto> getByParentCategoryId(Long parentCategoryId) {
        List<CategoryProductTemplateMapping> mappings = mappingRepository.findByParentCategoryIdWithDetails(parentCategoryId);
        return mappings.stream()
                .map(mapping -> CategoryProductTemplateDto.from(
                        mapping.getCategory(),
                        mapping.getProductTemplate(),
                        mapping.getId(),
                        mapping.getSortOrder()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별로 그룹화된 ProductTemplate 목록 조회
     * 하나의 카테고리에 여러 개의 ProductTemplate이 매핑될 수 있음
     */
    @Transactional(readOnly = true)
    public List<CategoryWithProductTemplatesDto> getGroupedByCategory() {
        List<CategoryProductTemplateMapping> mappings = mappingRepository.findAllEnabledWithDetails();
        
        // 카테고리별로 그룹화
        Map<Long, List<CategoryProductTemplateMapping>> groupedByCategory = mappings.stream()
                .collect(Collectors.groupingBy(m -> m.getCategory().getId()));
        
        return groupedByCategory.entrySet().stream()
                .map(entry -> {
                    CategoryProductTemplateMapping firstMapping = entry.getValue().get(0);
                    List<ProductTemplateDto> productTemplates = entry.getValue().stream()
                            .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                            .map(mapping -> ProductTemplateDto.builder()
                                    .id(mapping.getProductTemplate().getId())
                                    .name(mapping.getProductTemplate().getName())
                                    .description(mapping.getProductTemplate().getDescription())
                                    .status(mapping.getProductTemplate().getStatus() != null 
                                            ? mapping.getProductTemplate().getStatus().name() 
                                            : null)
                                    .sortOrder(mapping.getSortOrder())
                                    .mappingId(mapping.getId())
                                    .build())
                            .collect(Collectors.toList());
                    
                    return CategoryWithProductTemplatesDto.builder()
                            .categoryId(firstMapping.getCategory().getId())
                            .categoryName(firstMapping.getCategory().getName())
                            .categoryDescription(firstMapping.getCategory().getDescription())
                            .categoryDisplayOrder(firstMapping.getCategory().getDisplayOrder())
                            .categoryEnabled(firstMapping.getCategory().getEnabled())
                            .productTemplates(productTemplates)
                            .build();
                })
                .sorted((a, b) -> {
                    int orderCompare = Integer.compare(
                            a.getCategoryDisplayOrder() != null ? a.getCategoryDisplayOrder() : 0,
                            b.getCategoryDisplayOrder() != null ? b.getCategoryDisplayOrder() : 0
                    );
                    return orderCompare != 0 ? orderCompare : Long.compare(a.getCategoryId(), b.getCategoryId());
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리의 ProductTemplate 목록 조회
     * 하나의 카테고리에 여러 개의 ProductTemplate이 매핑될 수 있음
     */
    @Transactional(readOnly = true)
    public CategoryWithProductTemplatesDto getByCategoryIdGrouped(Long categoryId) {
        List<CategoryProductTemplateMapping> mappings = mappingRepository.findByCategoryIdWithDetails(categoryId);
        
        if (mappings.isEmpty()) {
            return null;
        }
        
        CategoryProductTemplateMapping firstMapping = mappings.get(0);
        List<ProductTemplateDto> productTemplates = mappings.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .map(mapping -> ProductTemplateDto.builder()
                        .id(mapping.getProductTemplate().getId())
                        .name(mapping.getProductTemplate().getName())
                        .description(mapping.getProductTemplate().getDescription())
                        .status(mapping.getProductTemplate().getStatus() != null 
                                ? mapping.getProductTemplate().getStatus().name() 
                                : null)
                        .sortOrder(mapping.getSortOrder())
                        .mappingId(mapping.getId())
                        .build())
                .collect(Collectors.toList());
        
        return CategoryWithProductTemplatesDto.builder()
                .categoryId(firstMapping.getCategory().getId())
                .categoryName(firstMapping.getCategory().getName())
                .categoryDescription(firstMapping.getCategory().getDescription())
                .categoryDisplayOrder(firstMapping.getCategory().getDisplayOrder())
                .categoryEnabled(firstMapping.getCategory().getEnabled())
                .productTemplates(productTemplates)
                .build();
    }
}

