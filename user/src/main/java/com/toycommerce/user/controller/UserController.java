package com.toycommerce.user.controller;

import com.toycommerce.common.annotation.RequireAuth;
import com.toycommerce.common.annotation.RequireRole;
import com.toycommerce.common.entity.user.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "사용자", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Operation(summary = "헬스 체크", description = "서비스 상태 확인용 공개 API")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from User Service");
    }

    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/profile")
    @RequireAuth
    public Map<String, Object> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "message", "프로필 조회 성공",
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }

    @Operation(summary = "내 주문 내역 조회", description = "로그인한 사용자의 주문 내역을 조회합니다. (USER 권한 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/my-orders")
    @RequireRole(Role.USER)
    public Map<String, String> getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "message", "주문 내역 조회 성공",
                "username", authentication.getName()
        );
    }

    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자 목록을 조회합니다. (ADMIN 권한 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/admin/users")
    @RequireRole(Role.ADMIN)
    public Map<String, String> getAllUsers() {
        return Map.of("message", "전체 사용자 목록 조회 (ADMIN 전용)");
    }

    @Operation(summary = "설정 조회", description = "사용자 설정을 조회합니다. (USER 또는 ADMIN 권한 필요)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/settings")
    @RequireRole({Role.USER, Role.ADMIN})
    public Map<String, String> getSettings() {
        return Map.of("message", "설정 조회 성공 (USER 또는 ADMIN)");
    }
}
