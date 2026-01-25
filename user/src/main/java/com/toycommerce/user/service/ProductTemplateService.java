package com.toycommerce.user.service;

import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductTemplate;
import com.toycommerce.common.entity.category.CategoryProductTemplateMapping;
import com.toycommerce.user.dto.ProductTemplateDetailDto;
import com.toycommerce.user.repository.ProductRepository;
import com.toycommerce.user.repository.ProductTemplateRepository;
import com.toycommerce.user.repository.CategoryProductTemplateMappingRepository;
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
    private final ProductRepository productRepository;
    private final CategoryProductTemplateMappingRepository categoryProductTemplateMappingRepository;

    @Transactional(readOnly = true)
    public ProductTemplateDetailDto getProductTemplateDetail(Long templateId) {
        ProductTemplate template = productTemplateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("ProductTemplate not found: " + templateId));

        // ProductTemplate에 속한 Product 목록 조회
        List<Product> products = productRepository.findByProductTemplateId(templateId);
        List<ProductTemplateDetailDto.ProductInfoDto> productDtos = products.stream()
                .filter(product -> product.getStatus() != null && 
                        product.getStatus().name().equals("ACTIVE"))
                .map(product -> ProductTemplateDetailDto.ProductInfoDto.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .productDescription(product.getDescription())
                        .sku(product.getSku())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .status(product.getStatus() != null ? product.getStatus().name() : null)
                        .build())
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
                .products(productDtos)
                .categories(categories)
                .build();
    }
}

