package com.toycommerce.admin.service;

import com.toycommerce.admin.dto.product.ImageRequest;
import com.toycommerce.admin.dto.product.AttachmentResponse;
import com.toycommerce.admin.repository.AttachmentRepository;
import com.toycommerce.admin.repository.ProductAttachmentRepository;
import com.toycommerce.admin.repository.ProductRepository;
import com.toycommerce.common.entity.attachment.Attachment;
import com.toycommerce.common.entity.attachment.AttachmentType;
import com.toycommerce.common.entity.attachment.ProductAttachment;
import com.toycommerce.common.entity.enums.EntityStatus;
import com.toycommerce.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ProductAttachmentRepository productAttachmentRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    /**
     * 상품 이미지 목록 조회
     */
    public List<AttachmentResponse> getProductImages(Long productId) {
        List<ProductAttachment> productAttachments = productAttachmentRepository.findActiveByProductId(productId);
        return productAttachments.stream()
                .map(pa -> AttachmentResponse.from(pa.getAttachment(), pa))
                .toList();
    }

    /**
     * 상품 이미지 업로드 (리스트)
     */
    @Transactional
    public List<AttachmentResponse> uploadProductImages(Long productId, List<ImageRequest> imageRequests) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        List<AttachmentResponse> responses = new ArrayList<>();
        Integer maxSortOrder = productAttachmentRepository.findMaxSortOrderByProductId(productId);
        int sortOrder = maxSortOrder != null ? maxSortOrder + 1 : 0;

        for (ImageRequest request : imageRequests) {
            // 파일 저장
            Attachment attachment = fileStorageService.saveBase64Image(
                    request.getBase64Data(),
                    request.getOriginalName()
            );
            attachment = attachmentRepository.save(attachment);

            // 첨부파일 타입 결정
            AttachmentType attachmentType = request.getAttachmentType() != null 
                    ? request.getAttachmentType() 
                    : AttachmentType.PRODUCT_GALLERY;

            // 대표 이미지 여부 결정
            boolean isPrimary = request.getIsPrimary() != null && request.getIsPrimary();
            
            // 대표 이미지로 설정할 경우 기존 대표 이미지 해제
            if (isPrimary) {
                productAttachmentRepository.findByProductIdAndIsPrimaryTrue(productId)
                        .ifPresent(pa -> pa.setPrimary(false));
            }

            // 상품-첨부파일 매핑 생성
            ProductAttachment productAttachment = ProductAttachment.builder()
                    .product(product)
                    .attachment(attachment)
                    .attachmentType(attachmentType)
                    .sortOrder(sortOrder++)
                    .isPrimary(isPrimary)
                    .build();
            productAttachment = productAttachmentRepository.save(productAttachment);

            responses.add(AttachmentResponse.from(attachment, productAttachment));
            log.info("상품 이미지 업로드: productId={}, attachmentId={}", productId, attachment.getId());
        }

        return responses;
    }

    /**
     * 상품 이미지 삭제
     */
    @Transactional
    public void deleteProductImage(Long productId, Long attachmentId) {
        ProductAttachment productAttachment = productAttachmentRepository.findActiveByProductId(productId).stream()
                .filter(pa -> pa.getAttachment().getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 상품의 이미지를 찾을 수 없습니다."));

        Attachment attachment = productAttachment.getAttachment();
        
        // 매핑 삭제
        productAttachmentRepository.delete(productAttachment);
        
        // 파일 삭제 (소프트 삭제)
        attachment.delete();
        attachmentRepository.save(attachment);
        
        // 실제 파일 삭제
        fileStorageService.deleteFile(attachment);
        
        log.info("상품 이미지 삭제: productId={}, attachmentId={}", productId, attachmentId);
    }

    /**
     * 상품 대표 이미지 설정
     */
    @Transactional
    public AttachmentResponse setPrimaryImage(Long productId, Long attachmentId) {
        // 기존 대표 이미지 해제
        productAttachmentRepository.findByProductIdAndIsPrimaryTrue(productId)
                .ifPresent(pa -> pa.setPrimary(false));

        // 새 대표 이미지 설정
        ProductAttachment productAttachment = productAttachmentRepository.findActiveByProductId(productId).stream()
                .filter(pa -> pa.getAttachment().getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 상품의 이미지를 찾을 수 없습니다."));

        productAttachment.setPrimary(true);
        
        log.info("상품 대표 이미지 설정: productId={}, attachmentId={}", productId, attachmentId);
        return AttachmentResponse.from(productAttachment.getAttachment(), productAttachment);
    }

    /**
     * 상품 이미지 순서 변경
     */
    @Transactional
    public List<AttachmentResponse> reorderProductImages(Long productId, List<Long> attachmentIds) {
        List<ProductAttachment> productAttachments = productAttachmentRepository.findActiveByProductId(productId);
        
        for (int i = 0; i < attachmentIds.size(); i++) {
            Long attachmentId = attachmentIds.get(i);
            int newSortOrder = i;
            
            productAttachments.stream()
                    .filter(pa -> pa.getAttachment().getId().equals(attachmentId))
                    .findFirst()
                    .ifPresent(pa -> pa.updateSortOrder(newSortOrder));
        }
        
        log.info("상품 이미지 순서 변경: productId={}", productId);
        
        return productAttachmentRepository.findActiveByProductId(productId).stream()
                .map(pa -> AttachmentResponse.from(pa.getAttachment(), pa))
                .toList();
    }

    /**
     * 상품의 모든 이미지 삭제 (상품 삭제 시)
     */
    @Transactional
    public void deleteAllProductImages(Long productId) {
        List<ProductAttachment> productAttachments = productAttachmentRepository.findActiveByProductId(productId);
        
        for (ProductAttachment pa : productAttachments) {
            Attachment attachment = pa.getAttachment();
            attachment.delete();
            fileStorageService.deleteFile(attachment);
        }
        
        log.info("상품의 모든 이미지 삭제: productId={}, count={}", productId, productAttachments.size());
    }
}

