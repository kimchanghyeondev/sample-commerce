package com.toycommerce.gateway.service;

import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import com.toycommerce.common.entity.product.ProductTemplate;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.gateway.dto.ProductOptionDto;
import com.toycommerce.gateway.dto.ProductOptionGroupDto;
import com.toycommerce.gateway.dto.ProductTemplateDetailDto;
import com.toycommerce.gateway.repository.ProductOptionGroupRepository;
import com.toycommerce.gateway.repository.ProductOptionRepository;
import com.toycommerce.gateway.repository.ProductTemplateRepository;
import com.toycommerce.gateway.repository.CategoryProductTemplateMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductTemplateService {

    private final ProductTemplateRepository productTemplateRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CategoryProductTemplateMappingRepository categoryProductTemplateMappingRepository;

    @Transactional(readOnly = true)
    public ProductTemplateDetailDto getProductTemplateDetail(Long templateId) {
        ProductTemplate template = productTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("ProductTemplate not found: " + templateId));

        // 옵션 그룹 조회
        List<ProductOptionGroup> optionGroups = productOptionGroupRepository.findByProductTemplateId(templateId);
        
        // 옵션 그룹별로 옵션 매핑
        List<ProductOptionGroupDto> optionGroupDtos = optionGroups.stream()
                .filter(group -> group.getEntityStatus() != null && 
                        group.getEntityStatus().name().equals("ACTIVE"))
                .map(group -> {
                    // 각 옵션 그룹에 속한 옵션만 조회
                    List<ProductOption> groupOptions = productOptionRepository.findByProductOptionGroupId(group.getId());
                    List<ProductOptionDto> optionDtos = groupOptions.stream()
                            .filter(option -> option.getEntityStatus() != null && 
                                    option.getEntityStatus().name().equals("ACTIVE"))
                            .map(option -> ProductOptionDto.builder()
                                    .id(option.getId())
                                    .name(option.getName())
                                    .status(option.getEntityStatus() != null ? option.getEntityStatus().name() : null)
                                    .build())
                            .collect(Collectors.toList());
                    
                    return ProductOptionGroupDto.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .status(group.getEntityStatus() != null ? group.getEntityStatus().name() : null)
                            .options(optionDtos)
                            .build();
                })
                .collect(Collectors.toList());

        // 카테고리 정보 조회
        List<CategoryProductTemplateMapping> mappings = categoryProductTemplateMappingRepository.findAll().stream()
                .filter(m -> m.getProductTemplate().getId().equals(templateId))
                .collect(Collectors.toList());
        
        List<ProductTemplateDetailDto.CategoryInfoDto> categories = mappings.stream()
                .map(m -> ProductTemplateDetailDto.CategoryInfoDto.builder()
                        .categoryId(m.getCategory().getId())
                        .categoryName(m.getCategory().getName())
                        .build())
                .collect(Collectors.toList());

        return ProductTemplateDetailDto.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .status(template.getStatus() != null ? template.getStatus().name() : null)
                .optionGroups(optionGroupDtos)
                .categories(categories)
                .build();
    }
}

