package com.toycommerce.user.controller;

import com.toycommerce.user.dto.ProductDetailDto;
import com.toycommerce.user.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getProductDetail(@PathVariable Long id) {
        try {
            ProductDetailDto detail = productService.getProductDetail(id);
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<ProductDetailDto>> getProductsByTemplateId(@PathVariable Long templateId) {
        List<ProductDetailDto> products = productService.getProductsByTemplateId(templateId);
        return ResponseEntity.ok(products);
    }
}

