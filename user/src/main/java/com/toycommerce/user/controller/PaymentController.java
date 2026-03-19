package com.toycommerce.user.controller;

import com.toycommerce.common.annotation.RequireAuth;
import com.toycommerce.user.common.CustomAuthenticateManager;
import com.toycommerce.user.dto.PaymentRequest;
import com.toycommerce.user.dto.PaymentResponse;
import com.toycommerce.user.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "결제", description = "결제 관련 API")
@RestController
@RequestMapping("/api/user/payment")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomAuthenticateManager authenticateManager;

    @Operation(
            summary = "결제 생성",
            description = "새로운 결제를 생성합니다. 상품 옵션 검증, 재고 확인, 금액 계산이 자동으로 수행됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (검증 실패, 재고 부족 등)"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    @RequireAuth
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        Long userId = authenticateManager.getCurrentUsername();
        return ResponseEntity.ok(PaymentResponse.from(paymentService.createPayment(userId, request)));
    }

    @Operation(
            summary = "결제 조회",
            description = "특정 결제의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/{paymentId}")
    @RequireAuth
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "결제 ID", example = "1") @PathVariable Long paymentId) {
        Long userId = authenticateManager.getCurrentUsername();
        return ResponseEntity.ok(PaymentResponse.from(paymentService.getPayment(userId, paymentId)));
    }

    @Operation(
            summary = "결제 목록 조회",
            description = "현재 로그인한 사용자의 결제 목록을 조회합니다. 최신순으로 정렬됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping
    @RequireAuth
    public ResponseEntity<List<PaymentResponse>> getPayments() {
        Long userId = authenticateManager.getCurrentUsername();
        List<PaymentResponse> payments = paymentService.getPayments(userId).stream()
                .map(PaymentResponse::from)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @Operation(
            summary = "결제 취소",
            description = "결제를 취소합니다. PENDING 또는 COMPLETED 상태의 결제만 취소 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소할 수 없는 상태"),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/{paymentId}/cancel")
    @RequireAuth
    public ResponseEntity<PaymentResponse> cancelPayment(
            @Parameter(description = "결제 ID", example = "1") @PathVariable Long paymentId) {
        Long userId = authenticateManager.getCurrentUsername();
        return ResponseEntity.ok(PaymentResponse.from(paymentService.cancelPayment(userId, paymentId)));
    }

    @Operation(
            summary = "결제 완료 처리",
            description = "결제를 완료 상태로 변경합니다. 외부 PG사 연동 후 호출합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "완료 처리 성공"),
            @ApiResponse(responseCode = "400", description = "완료 처리할 수 없는 상태"),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping("/{paymentId}/complete")
    @RequireAuth
    public ResponseEntity<PaymentResponse> completePayment(
            @Parameter(description = "결제 ID", example = "1") @PathVariable Long paymentId,
            @RequestBody Map<String, String> paymentInfo) {
        Long userId = authenticateManager.getCurrentUsername();
        String paymentKey = paymentInfo.get("paymentKey");
        String transactionId = paymentInfo.get("transactionId");
        return ResponseEntity.ok(PaymentResponse.from(
                paymentService.completePayment(userId, paymentId, paymentKey, transactionId)));
    }
}

