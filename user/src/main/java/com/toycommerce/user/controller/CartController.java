package com.toycommerce.user.controller;

import com.toycommerce.common.annotation.RequireAuth;
import com.toycommerce.user.dto.CartDto;
import com.toycommerce.user.dto.CartItemDto;
import com.toycommerce.user.dto.CartItemRequest;
import com.toycommerce.user.dto.CartItemUpdateRequest;
import com.toycommerce.user.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "장바구니", description = "장바구니 관련 API")
@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회", description = "현재 로그인한 사용자의 장바구니를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping
    @RequireAuth
    public ResponseEntity<CartDto> getCart() {
        String username = getCurrentUsername();
        CartDto cart = cartService.getCart(username);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "장바구니에 상품 추가", description = "장바구니에 새로운 상품을 추가합니다. 이미 존재하는 상품이면 수량이 증가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "상품 옵션을 찾을 수 없음")
    })
    @PostMapping("/items")
    @RequireAuth
    public ResponseEntity<CartItemDto> addItem(@Valid @RequestBody CartItemRequest request) {
        String username = getCurrentUsername();
        CartItemDto item = cartService.addItem(username, request);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "장바구니 아이템 수량 수정", description = "장바구니 아이템의 수량을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    })
    @PatchMapping("/items/{cartItemId}")
    @RequireAuth
    public ResponseEntity<CartItemDto> updateItemQuantity(
            @Parameter(description = "장바구니 아이템 ID") @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequest request) {
        String username = getCurrentUsername();
        CartItemDto item = cartService.updateItemQuantity(username, cartItemId, request);
        return ResponseEntity.ok(item);
    }

    @Operation(summary = "장바구니 아이템 삭제", description = "장바구니에서 특정 아이템을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "아이템을 찾을 수 없음")
    })
    @DeleteMapping("/items/{cartItemId}")
    @RequireAuth
    public ResponseEntity<Map<String, String>> removeItem(
            @Parameter(description = "장바구니 아이템 ID") @PathVariable Long cartItemId) {
        String username = getCurrentUsername();
        cartService.removeItem(username, cartItemId);
        return ResponseEntity.ok(Map.of("message", "장바구니 아이템이 삭제되었습니다."));
    }

    @Operation(summary = "장바구니 비우기", description = "장바구니의 모든 아이템을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비우기 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @DeleteMapping
    @RequireAuth
    public ResponseEntity<Map<String, String>> clearCart() {
        String username = getCurrentUsername();
        cartService.clearCart(username);
        return ResponseEntity.ok(Map.of("message", "장바구니가 비워졌습니다."));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

