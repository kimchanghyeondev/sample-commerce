package com.toycommerce.user.controller;

import com.toycommerce.user.dto.ProductTemplateDetailDto;
import com.toycommerce.user.service.ProductTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 템플릿", description = "상품 템플릿 조회 API")
@RestController
@RequestMapping("/api/user/product-templates")
@RequiredArgsConstructor
public class ProductTemplateController {

    private final ProductTemplateService productTemplateService;

    @Operation(summary = "상품 템플릿 상세 조회", description = "상품 템플릿의 상세 정보(상품 목록 포함)를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품 템플릿을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductTemplateDetailDto> getProductTemplateDetail(
            @Parameter(description = "상품 템플릿 ID") @PathVariable Long id) {
        try {
            ProductTemplateDetailDto detail = productTemplateService.getProductTemplateDetail(id);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

