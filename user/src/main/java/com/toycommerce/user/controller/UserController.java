package com.toycommerce.user.controller;

import com.toycommerce.common.annotation.RequireAuth;
import com.toycommerce.common.annotation.RequireRole;
import com.toycommerce.common.entity.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 인증/인가가 필요하지 않은 공개 API
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from User Service");
    }

    /**
     * 인증이 필요한 API 예제
     * @RequireAuth 어노테이션을 사용하여 JWT 토큰 검증을 수행합니다.
     */
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

    /**
     * USER 역할이 필요한 API 예제
     * @RequireRole 어노테이션을 사용하여 특정 역할을 가진 사용자만 접근할 수 있습니다.
     */
    @GetMapping("/my-orders")
    @RequireRole(Role.USER)
    public Map<String, String> getMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Map.of(
                "message", "주문 내역 조회 성공",
                "username", authentication.getName()
        );
    }

    /**
     * ADMIN 역할이 필요한 API 예제
     */
    @GetMapping("/admin/users")
    @RequireRole(Role.ADMIN)
    public Map<String, String> getAllUsers() {
        return Map.of("message", "전체 사용자 목록 조회 (ADMIN 전용)");
    }

    /**
     * USER 또는 ADMIN 역할이 필요한 API 예제
     */
    @GetMapping("/settings")
    @RequireRole({Role.USER, Role.ADMIN})
    public Map<String, String> getSettings() {
        return Map.of("message", "설정 조회 성공 (USER 또는 ADMIN)");
    }
}
