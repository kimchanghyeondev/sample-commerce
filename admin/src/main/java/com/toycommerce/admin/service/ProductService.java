package com.toycommerce.admin.service;

import com.toycommerce.admin.dto.common.PageResponse;
import com.toycommerce.admin.dto.product.*;
import com.toycommerce.admin.repository.ProductAttachmentRepository;
import com.toycommerce.admin.repository.ProductOptionGroupRepository;
import com.toycommerce.admin.repository.ProductOptionRepository;
import com.toycommerce.admin.repository.ProductRepository;
import com.toycommerce.admin.repository.ProductTemplateRepository;
import com.toycommerce.common.entity.attachment.ProductAttachment;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.Product;
import com.toycommerce.common.entity.product.ProductOption;
import com.toycommerce.common.entity.product.ProductOptionGroup;
import com.toycommerce.common.entity.product.ProductTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionRepository optionRepository;
    private final ProductAttachmentRepository productAttachmentRepository;

    /**
     * 상품 목록 조회 (페이징)
     */
    public PageResponse<ProductResponse> getProducts(Pageable pageable) {
        Page<Product> page = productRepository.findAllActive(pageable);
        return PageResponse.from(page, product -> {
            String primaryImageUrl = getPrimaryImageUrl(product.getId());
            return ProductResponse.simpleFrom(product, primaryImageUrl);
        });
    }

    /**
     * 상품 템플릿별 상품 목록 조회
     */
    public List<ProductResponse> getProductsByTemplate(Long templateId) {
        List<Product> products = productRepository.findActiveByTemplateId(templateId);
        return products.stream()
                .map(product -> {
                    String primaryImageUrl = getPrimaryImageUrl(product.getId());
                    return ProductResponse.simpleFrom(product, primaryImageUrl);
                })
                .toList();
    }

    /**
     * 상품 검색
     */
    public PageResponse<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> page = productRepository.searchByName(keyword, pageable);
        return PageResponse.from(page, product -> {
            String primaryImageUrl = getPrimaryImageUrl(product.getId());
            return ProductResponse.simpleFrom(product, primaryImageUrl);
        });
    }

    /**
     * 상품 상세 조회 (옵션 그룹, 옵션, 이미지 포함)
     */
    public ProductResponse getProduct(Long id) {
        Product product = findById(id);
        List<ProductResponse.OptionGroupResponse> optionGroups = getOptionGroupResponses(id);
        List<AttachmentResponse> images = getImageResponses(id);
        return ProductResponse.from(product, optionGroups, images);
    }

    /**
     * 상품 생성
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        // SKU 중복 체크
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("이미 존재하는 SKU입니다: " + request.getSku());
        }

        // 상품 템플릿 조회
        ProductTemplate template = productTemplateRepository.findById(request.getProductTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("상품 템플릿을 찾을 수 없습니다: " + request.getProductTemplateId()));

        // 상품 생성
        Product product = Product.builder()
                .productTemplate(template)
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .stock(request.getStock())
                .status(EntityStatus.ACTIVE)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("상품 생성: id={}, name={}, sku={}", savedProduct.getId(), savedProduct.getName(), savedProduct.getSku());

        // 옵션 그룹 및 옵션 생성
        List<ProductResponse.OptionGroupResponse> optionGroupResponses = new ArrayList<>();
        if (request.getOptionGroups() != null && !request.getOptionGroups().isEmpty()) {
            for (ProductRequest.OptionGroupRequest groupRequest : request.getOptionGroups()) {
                ProductResponse.OptionGroupResponse groupResponse = createOptionGroupInternal(savedProduct, groupRequest);
                optionGroupResponses.add(groupResponse);
            }
        }

        return ProductResponse.from(savedProduct, optionGroupResponses);
    }

    /**
     * 상품 수정
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = findById(id);

        if (request.getName() != null) {
            product.updateName(request.getName());
        }
        if (request.getDescription() != null) {
            product.updateDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.updatePrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.updateStock(request.getStock());
        }

        log.info("상품 수정: id={}", id);
        List<ProductResponse.OptionGroupResponse> optionGroups = getOptionGroupResponses(id);
        return ProductResponse.from(product, optionGroups);
    }

    /**
     * 상품 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findById(id);
        product.updateStatus(EntityStatus.DELETED);
        log.info("상품 삭제: id={}", id);
    }

    /**
     * 상품 상태 변경
     */
    @Transactional
    public ProductResponse updateStatus(Long id, EntityStatus status) {
        Product product = findById(id);
        product.updateStatus(status);
        log.info("상품 상태 변경: id={}, status={}", id, status);
        List<ProductResponse.OptionGroupResponse> optionGroups = getOptionGroupResponses(id);
        return ProductResponse.from(product, optionGroups);
    }

    // ==================== 옵션 그룹 관련 ====================

    /**
     * 옵션 그룹 생성
     */
    @Transactional
    public ProductResponse.OptionGroupResponse createOptionGroup(OptionGroupRequest request) {
        Product product = findById(request.getProductId());

        // 중복 이름 체크
        if (optionGroupRepository.existsByProductIdAndName(product.getId(), request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 옵션 그룹명입니다: " + request.getName());
        }

        ProductRequest.OptionGroupRequest groupRequest = new ProductRequest.OptionGroupRequest(
                request.getName(), request.getOptions());
        return createOptionGroupInternal(product, groupRequest);
    }

    /**
     * 옵션 그룹 삭제
     */
    @Transactional
    public void deleteOptionGroup(Long groupId) {
        ProductOptionGroup group = optionGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("옵션 그룹을 찾을 수 없습니다: " + groupId));
        group.updateStatus(EntityStatus.DELETED);
        log.info("옵션 그룹 삭제: id={}", groupId);
    }

    // ==================== 옵션 관련 ====================

    /**
     * 옵션 생성
     */
    @Transactional
    public ProductResponse.OptionResponse createOption(OptionRequest request) {
        ProductOptionGroup group = optionGroupRepository.findById(request.getOptionGroupId())
                .orElseThrow(() -> new IllegalArgumentException("옵션 그룹을 찾을 수 없습니다: " + request.getOptionGroupId()));

        // 중복 이름 체크
        if (optionRepository.existsByProductOptionGroupIdAndName(group.getId(), request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 옵션명입니다: " + request.getName());
        }

        ProductOption option = ProductOption.builder()
                .productOptionGroup(group)
                .name(request.getName())
                .status(EntityStatus.ACTIVE)
                .build();

        ProductOption savedOption = optionRepository.save(option);
        log.info("옵션 생성: id={}, name={}, groupId={}", savedOption.getId(), savedOption.getName(), group.getId());

        return ProductResponse.OptionResponse.from(savedOption);
    }

    /**
     * 옵션 삭제
     */
    @Transactional
    public void deleteOption(Long optionId) {
        ProductOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다: " + optionId));
        option.updateStatus(EntityStatus.DELETED);
        log.info("옵션 삭제: id={}", optionId);
    }

    // ==================== Private Methods ====================

    private Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));
    }

    private List<ProductResponse.OptionGroupResponse> getOptionGroupResponses(Long productId) {
        List<ProductOptionGroup> groups = optionGroupRepository.findActiveByProductId(productId);
        return groups.stream()
                .map(group -> {
                    List<ProductOption> options = optionRepository.findActiveByGroupId(group.getId());
                    return ProductResponse.OptionGroupResponse.from(group, options);
                })
                .toList();
    }

    private List<AttachmentResponse> getImageResponses(Long productId) {
        List<ProductAttachment> productAttachments = productAttachmentRepository.findActiveByProductId(productId);
        return productAttachments.stream()
                .map(pa -> AttachmentResponse.from(pa.getAttachment(), pa))
                .toList();
    }

    private String getPrimaryImageUrl(Long productId) {
        return productAttachmentRepository.findActivePrimaryByProductId(productId)
                .map(pa -> pa.getAttachment().getFileUrl())
                .orElseGet(() -> {
                    // 대표 이미지가 없으면 첫 번째 이미지 URL 반환
                    List<ProductAttachment> attachments = productAttachmentRepository.findActiveByProductId(productId);
                    return attachments.isEmpty() ? null : attachments.get(0).getAttachment().getFileUrl();
                });
    }

    private ProductResponse.OptionGroupResponse createOptionGroupInternal(Product product, ProductRequest.OptionGroupRequest request) {
        ProductOptionGroup group = ProductOptionGroup.builder()
                .product(product)
                .name(request.getName())
                .status(EntityStatus.ACTIVE)
                .build();

        ProductOptionGroup savedGroup = optionGroupRepository.save(group);
        log.info("옵션 그룹 생성: id={}, name={}, productId={}", savedGroup.getId(), savedGroup.getName(), product.getId());

        // 옵션 생성
        List<ProductOption> options = new ArrayList<>();
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            for (String optionName : request.getOptions()) {
                ProductOption option = ProductOption.builder()
                        .productOptionGroup(savedGroup)
                        .name(optionName)
                        .status(EntityStatus.ACTIVE)
                        .build();
                options.add(optionRepository.save(option));
                log.info("옵션 생성: name={}, groupId={}", optionName, savedGroup.getId());
            }
        }

        return ProductResponse.OptionGroupResponse.from(savedGroup, options);
    }
}

