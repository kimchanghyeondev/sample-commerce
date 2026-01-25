package com.toycommerce.user.controller;

import com.toycommerce.user.dto.ProductTemplateDetailDto;
import com.toycommerce.user.service.ProductTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/product-templates")
@RequiredArgsConstructor
public class ProductTemplateController {

    private final ProductTemplateService productTemplateService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductTemplateDetailDto> getProductTemplateDetail(@PathVariable Long id) {
        try {
            ProductTemplateDetailDto detail = productTemplateService.getProductTemplateDetail(id);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

