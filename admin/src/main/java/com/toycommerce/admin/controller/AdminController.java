package com.toycommerce.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "관리자", description = "관리자 서비스 API")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Operation(summary = "헬스 체크", description = "관리자 서비스 상태 확인용 API")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello from Admin Service");
    }
}

