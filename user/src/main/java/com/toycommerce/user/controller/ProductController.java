package com.toycommerce.user.controller;

import com.toycommerce.user.dto.ProductDetailDto;
import com.toycommerce.user.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품", description = "상품 조회 API")
@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 상세 조회", description = "상품의 상세 정보(옵션 포함)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getProductDetail(
            @Parameter(description = "상품 ID") @PathVariable Long id) {
        try {
            ProductDetailDto detail = productService.getProductDetail(id);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "템플릿별 상품 목록 조회", description = "특정 상품 템플릿에 속한 모든 상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<ProductDetailDto>> getProductsByTemplateId(
            @Parameter(description = "상품 템플릿 ID") @PathVariable Long templateId) {
        List<ProductDetailDto> products = productService.getProductsByTemplateId(templateId);
        return ResponseEntity.ok(products);
    }
}

