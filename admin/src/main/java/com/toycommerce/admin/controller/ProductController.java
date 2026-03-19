package com.toycommerce.admin.controller;

import com.toycommerce.admin.dto.common.PageResponse;
import com.toycommerce.admin.dto.product.*;
import com.toycommerce.admin.service.AttachmentService;
import com.toycommerce.admin.service.ProductService;
import com.toycommerce.common.entity.enums.EntityStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 상품 관리 API (관리자용)
 */
@Tag(name = "상품 관리", description = "상품 CRUD 및 이미지 관리 API")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AttachmentService attachmentService;

    // ==================== 상품 API ====================

    @Operation(summary = "상품 목록 조회", description = "페이징을 적용하여 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getProducts(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방향 (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<ProductResponse> response = productService.getProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "템플릿별 상품 목록 조회", description = "특정 상품 템플릿에 속한 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/by-template/{templateId}")
    public ResponseEntity<List<ProductResponse>> getProductsByTemplate(
            @Parameter(description = "상품 템플릿 ID") @PathVariable Long templateId) {
        List<ProductResponse> response = productService.getProductsByTemplate(templateId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 검색", description = "키워드로 상품을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ProductResponse> response = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회합니다. (옵션, 이미지 포함)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "상품 ID") @PathVariable Long id) {
        ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "상품 ID") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다. (소프트 삭제)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @Parameter(description = "상품 ID") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }

    @Operation(summary = "상품 상태 변경", description = "상품의 상태를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "상태 변경 성공")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateStatus(
            @Parameter(description = "상품 ID") @PathVariable Long id,
            @Parameter(description = "변경할 상태") @RequestParam EntityStatus status) {
        ProductResponse response = productService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // ==================== 상품 이미지 API ====================

    @Operation(summary = "상품 이미지 목록 조회", description = "상품의 이미지 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<AttachmentResponse>> getProductImages(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        List<AttachmentResponse> images = attachmentService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "상품 이미지 업로드", description = "상품에 이미지를 업로드합니다. (Base64 인코딩된 이미지 리스트)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PostMapping("/{productId}/images")
    public ResponseEntity<List<AttachmentResponse>> uploadProductImages(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Valid @RequestBody List<ImageRequest> imageRequests) {
        List<AttachmentResponse> images = attachmentService.uploadProductImages(productId, imageRequests);
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "상품 이미지 삭제", description = "상품의 특정 이미지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    @DeleteMapping("/{productId}/images/{attachmentId}")
    public ResponseEntity<Map<String, String>> deleteProductImage(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "첨부파일 ID") @PathVariable Long attachmentId) {
        attachmentService.deleteProductImage(productId, attachmentId);
        return ResponseEntity.ok(Map.of("message", "이미지가 삭제되었습니다."));
    }

    @Operation(summary = "대표 이미지 설정", description = "상품의 대표 이미지를 설정합니다.")
    @ApiResponse(responseCode = "200", description = "설정 성공")
    @PatchMapping("/{productId}/images/{attachmentId}/primary")
    public ResponseEntity<AttachmentResponse> setPrimaryImage(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "첨부파일 ID") @PathVariable Long attachmentId) {
        AttachmentResponse image = attachmentService.setPrimaryImage(productId, attachmentId);
        return ResponseEntity.ok(image);
    }

    @Operation(summary = "이미지 순서 변경", description = "상품 이미지의 순서를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "순서 변경 성공")
    @PatchMapping("/{productId}/images/reorder")
    public ResponseEntity<List<AttachmentResponse>> reorderProductImages(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "정렬된 첨부파일 ID 목록") @RequestBody List<Long> attachmentIds) {
        List<AttachmentResponse> images = attachmentService.reorderProductImages(productId, attachmentIds);
        return ResponseEntity.ok(images);
    }

    // ==================== 옵션 그룹 API ====================

    /**
     * 옵션 그룹 생성
     */
    @PostMapping("/option-groups")
    public ResponseEntity<ProductResponse.OptionGroupResponse> createOptionGroup(
            @Valid @RequestBody OptionGroupRequest request) {
        ProductResponse.OptionGroupResponse response = productService.createOptionGroup(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 옵션 그룹 삭제
     */
    @DeleteMapping("/option-groups/{groupId}")
    public ResponseEntity<Map<String, String>> deleteOptionGroup(@PathVariable Long groupId) {
        productService.deleteOptionGroup(groupId);
        return ResponseEntity.ok(Map.of("message", "옵션 그룹이 삭제되었습니다."));
    }

    // ==================== 옵션 API ====================

    /**
     * 옵션 생성
     */
    @PostMapping("/options")
    public ResponseEntity<ProductResponse.OptionResponse> createOption(
            @Valid @RequestBody OptionRequest request) {
        ProductResponse.OptionResponse response = productService.createOption(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 옵션 삭제
     */
    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Map<String, String>> deleteOption(@PathVariable Long optionId) {
        productService.deleteOption(optionId);
        return ResponseEntity.ok(Map.of("message", "옵션이 삭제되었습니다."));
    }
}

