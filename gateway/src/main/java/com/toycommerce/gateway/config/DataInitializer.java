package com.toycommerce.gateway.config;

import com.toycommerce.common.entity.user.Role;
import com.toycommerce.gateway.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;

    @PostConstruct
    @Transactional
    public void init() {
        initUsers();
    }

    private void initUsers() {
        try {
            // 기존 계정이 있어도 비밀번호를 업데이트합니다
            userService.createOrUpdateUser("admin", "admin", Role.ADMIN);
            log.info("Admin user initialized: admin/admin");
            
            userService.createOrUpdateUser("user", "user", Role.USER);
            log.info("User initialized: user/user");
        } catch (Exception e) {
            log.error("Failed to initialize users", e);
        }
    }
}
