package com.toycommerce.user.service;

import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import com.toycommerce.user.dto.ProductOptionDto;
import com.toycommerce.user.dto.ProductOptionGroupDto;
import com.toycommerce.user.dto.ProductDetailDto;
import com.toycommerce.user.repository.ProductRepository;
import com.toycommerce.user.repository.ProductOptionGroupRepository;
import com.toycommerce.user.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional(readOnly = true)
    public ProductDetailDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // 옵션 그룹 조회
        List<ProductOptionGroup> optionGroups = productOptionGroupRepository.findByProductId(productId);
        
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

        return ProductDetailDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .productTemplateId(product.getProductTemplate().getId())
                .productTemplateName(product.getProductTemplate().getName())
                .optionGroups(optionGroupDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductDetailDto> getProductsByTemplateId(Long templateId) {
        List<Product> products = productRepository.findByProductTemplateId(templateId);
        return products.stream()
                .map(product -> {
                    List<ProductOptionGroup> optionGroups = productOptionGroupRepository.findByProductId(product.getId());
                    List<ProductOptionGroupDto> optionGroupDtos = optionGroups.stream()
                            .filter(group -> group.getEntityStatus() != null && 
                                    group.getEntityStatus().name().equals("ACTIVE"))
                            .map(group -> {
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

                    return ProductDetailDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .sku(product.getSku())
                            .price(product.getPrice())
                            .stock(product.getStock())
                            .status(product.getStatus() != null ? product.getStatus().name() : null)
                            .productTemplateId(product.getProductTemplate().getId())
                            .productTemplateName(product.getProductTemplate().getName())
                            .optionGroups(optionGroupDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

