package com.toycommerce.admin.controller;

import com.toycommerce.admin.dto.common.PageResponse;
import com.toycommerce.admin.dto.product.ProductTemplateRequest;
import com.toycommerce.admin.dto.product.ProductTemplateResponse;
import com.toycommerce.admin.service.ProductTemplateService;
import com.toycommerce.common.entity.enums.EntityStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 상품 템플릿 관리 API (관리자용)
 */
@RestController
@RequestMapping("/api/admin/product-templates")
@RequiredArgsConstructor
public class ProductTemplateController {

    private final ProductTemplateService productTemplateService;

    /**
     * 상품 템플릿 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<PageResponse<ProductTemplateResponse>> getProductTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<ProductTemplateResponse> response = productTemplateService.getProductTemplates(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 템플릿 검색
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductTemplateResponse>> searchProductTemplates(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ProductTemplateResponse> response = productTemplateService.searchProductTemplates(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 템플릿 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductTemplateResponse> getProductTemplate(@PathVariable Long id) {
        ProductTemplateResponse response = productTemplateService.getProductTemplate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 템플릿 생성
     */
    @PostMapping
    public ResponseEntity<ProductTemplateResponse> createProductTemplate(
            @Valid @RequestBody ProductTemplateRequest request) {
        ProductTemplateResponse response = productTemplateService.createProductTemplate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 템플릿 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductTemplateResponse> updateProductTemplate(
            @PathVariable Long id,
            @Valid @RequestBody ProductTemplateRequest request) {
        ProductTemplateResponse response = productTemplateService.updateProductTemplate(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 템플릿 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProductTemplate(@PathVariable Long id) {
        productTemplateService.deleteProductTemplate(id);
        return ResponseEntity.ok(Map.of("message", "상품 템플릿이 삭제되었습니다."));
    }

    /**
     * 상품 템플릿 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductTemplateResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam EntityStatus status) {
        ProductTemplateResponse response = productTemplateService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }
}

