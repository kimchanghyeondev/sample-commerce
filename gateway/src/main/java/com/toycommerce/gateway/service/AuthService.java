package com.toycommerce.gateway.service;

import com.toycommerce.common.entity.user.User;
import com.toycommerce.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public Map<String, String> login(String username, String password) {
        try {
            // 사용자 조회
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Category not found: {}", username);
                        return new RuntimeException("Invalid credentials");
                    });

            // 사용자 활성화 여부 확인
            if (!user.getEnabled()) {
                log.error("Category is disabled: {}", username);
                throw new RuntimeException("Category is disabled");
            }

            // 비밀번호 검증
            if (!userService.validatePassword(user, password)) {
                log.error("Invalid password for user: {}", username);
                throw new RuntimeException("Invalid credentials");
            }

            // JWT 토큰 생성
            List<String> roles = List.of(user.getRole().name());
            String token = jwtUtil.generateToken(username, roles);
            
            return Map.of("token", token);
        } catch (RuntimeException e) {
            log.error("Authentication failed for user: {}", username, e);
            throw e;
        } catch (Exception e) {
            log.error("Error during authentication for user: {}", username, e);
            throw new RuntimeException("Authentication failed", e);
        }
    }
}

